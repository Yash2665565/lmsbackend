package com.school.fees.service;

import com.school.fees.dto.*;
import com.school.fees.entity.FeeHead;
import com.school.fees.entity.FeeStructure;
import com.school.fees.entity.StudentFee;
import com.school.fees.repository.FeeHeadRepository;
import com.school.fees.repository.FeeStructureRepository;
import com.school.fees.repository.StudentFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeesService {

    private final FeeHeadRepository headRepo;
    private final FeeStructureRepository structureRepo;
    private final StudentFeeRepository studentFeeRepo;

    private static BigDecimal nz(BigDecimal b) { return b == null ? BigDecimal.ZERO : b; }

    private static String deriveStatus(BigDecimal amount, BigDecimal paid) {
        amount = nz(amount); paid = nz(paid);
        if (paid.compareTo(amount) >= 0 && amount.signum() > 0) return "PAID";
        if (paid.signum() > 0) return "PARTIAL";
        return "PENDING";
    }

    /* ── Fee heads ── */
    public List<FeeHeadDto> listHeads() {
        return headRepo.findAll().stream()
                .map(h -> FeeHeadDto.builder().id(h.getId()).name(h.getName()).description(h.getDescription()).build())
                .collect(Collectors.toList());
    }

    @Transactional
    public FeeHeadDto createHead(String name, String description) {
        FeeHead h = new FeeHead(); h.setName(name); h.setDescription(description);
        h = headRepo.save(h);
        return FeeHeadDto.builder().id(h.getId()).name(h.getName()).description(h.getDescription()).build();
    }

    @Transactional
    public FeeHeadDto updateHead(Long id, String name, String description) {
        FeeHead h = headRepo.findById(id).orElseThrow(() -> new RuntimeException("Fee head not found"));
        h.setName(name); h.setDescription(description);
        h = headRepo.save(h);
        return FeeHeadDto.builder().id(h.getId()).name(h.getName()).description(h.getDescription()).build();
    }

    @Transactional
    public void deleteHead(Long id) { headRepo.deleteById(id); }

    /* ── Structure per section ── */
    public List<FeeStructureDto> listStructures(Long sectionId) {
        Map<Long, String> names = headRepo.findAll().stream()
                .collect(Collectors.toMap(FeeHead::getId, FeeHead::getName));
        return structureRepo.findBySectionId(sectionId).stream()
                .map(s -> FeeStructureDto.builder()
                        .id(s.getId()).sectionId(s.getSectionId()).feeHeadId(s.getFeeHeadId())
                        .feeHeadName(names.get(s.getFeeHeadId()))
                        .amount(s.getAmount()).frequency(s.getFrequency()).build())
                .collect(Collectors.toList());
    }

    @Transactional
    public FeeStructureDto upsertStructure(Long sectionId, Long feeHeadId, BigDecimal amount, String frequency) {
        FeeStructure s = structureRepo.findBySectionId(sectionId).stream()
                .filter(x -> x.getFeeHeadId().equals(feeHeadId)).findFirst()
                .orElseGet(FeeStructure::new);
        s.setSectionId(sectionId);
        s.setFeeHeadId(feeHeadId);
        s.setAmount(nz(amount));
        s.setFrequency(frequency == null ? "Annual" : frequency);
        s = structureRepo.save(s);
        String name = headRepo.findById(feeHeadId).map(FeeHead::getName).orElse(null);
        return FeeStructureDto.builder()
                .id(s.getId()).sectionId(s.getSectionId()).feeHeadId(s.getFeeHeadId())
                .feeHeadName(name).amount(s.getAmount()).frequency(s.getFrequency()).build();
    }

    @Transactional
    public void deleteStructure(Long id) { structureRepo.deleteById(id); }

    /* ── Student-facing fee sheet ── */
    public StudentFeeDto getStudentFees(Long studentId) {
        Long sectionId = studentFeeRepo.findSectionIdByStudent(studentId);
        StudentFeeDto.StudentFeeDtoBuilder out = StudentFeeDto.builder().studentId(studentId);
        if (sectionId == null) {
            return out.totalAmount(BigDecimal.ZERO).totalPaid(BigDecimal.ZERO).totalDue(BigDecimal.ZERO)
                    .lines(List.of()).build();
        }
        Map<Long, String> headNames = headRepo.findAll().stream()
                .collect(Collectors.toMap(FeeHead::getId, FeeHead::getName));
        List<FeeStructure> structures = structureRepo.findBySectionId(sectionId);
        Map<Long, StudentFee> paid = studentFeeRepo.findByStudentId(studentId).stream()
                .collect(Collectors.toMap(StudentFee::getFeeStructureId, x -> x, (a, b) -> a));

        BigDecimal totalAmount = BigDecimal.ZERO, totalPaid = BigDecimal.ZERO;
        java.util.List<StudentFeeDto.Line> lines = new java.util.ArrayList<>();
        for (FeeStructure s : structures) {
            StudentFee sf = paid.get(s.getId());
            BigDecimal amount = nz(s.getAmount());
            BigDecimal ap = sf == null ? BigDecimal.ZERO : nz(sf.getAmountPaid());
            String status = sf != null && sf.getStatus() != null ? sf.getStatus() : deriveStatus(amount, ap);
            BigDecimal due = amount.subtract(ap).max(BigDecimal.ZERO);
            totalAmount = totalAmount.add(amount);
            totalPaid = totalPaid.add(ap);
            lines.add(StudentFeeDto.Line.builder()
                    .feeStructureId(s.getId())
                    .feeHeadName(headNames.get(s.getFeeHeadId()))
                    .frequency(s.getFrequency())
                    .amount(amount).amountPaid(ap).due(due).status(status)
                    .remarks(sf == null ? null : sf.getRemarks())
                    .build());
        }
        return out.totalAmount(totalAmount).totalPaid(totalPaid)
                .totalDue(totalAmount.subtract(totalPaid).max(BigDecimal.ZERO))
                .lines(lines).build();
    }

    /* ── Admin collections per section ── */
    public List<CollectionRowDto> getSectionCollections(Long sectionId) {
        List<FeeStructure> structures = structureRepo.findBySectionId(sectionId);
        BigDecimal total = structures.stream().map(s -> nz(s.getAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        java.util.Set<Long> structIds = structures.stream().map(FeeStructure::getId).collect(Collectors.toSet());

        List<StudentLite> students = studentFeeRepo.findStudentsBySection(sectionId);
        java.util.List<CollectionRowDto> rows = new java.util.ArrayList<>();
        for (StudentLite st : students) {
            BigDecimal paid = studentFeeRepo.findByStudentId(st.getStudentId()).stream()
                    .filter(sf -> structIds.contains(sf.getFeeStructureId()))
                    .map(sf -> nz(sf.getAmountPaid())).reduce(BigDecimal.ZERO, BigDecimal::add);
            rows.add(CollectionRowDto.builder()
                    .studentId(st.getStudentId())
                    .studentName(st.getStudentName())
                    .admissionNo(st.getAdmissionNo())
                    .totalAmount(total).totalPaid(paid)
                    .totalDue(total.subtract(paid).max(BigDecimal.ZERO))
                    .status(deriveStatus(total, paid))
                    .build());
        }
        return rows;
    }

    @Transactional
    public void updateStudentFeeStatus(Long studentId, Long feeStructureId, String status,
                                       BigDecimal amountPaid, String remarks) {
        StudentFee sf = studentFeeRepo.findByStudentIdAndFeeStructureId(studentId, feeStructureId)
                .orElseGet(StudentFee::new);
        sf.setStudentId(studentId);
        sf.setFeeStructureId(feeStructureId);
        sf.setAmountPaid(nz(amountPaid));
        sf.setRemarks(remarks);
        String resolved = (status != null && !status.isBlank())
                ? status
                : deriveStatus(structureRepo.findById(feeStructureId).map(FeeStructure::getAmount).orElse(BigDecimal.ZERO), nz(amountPaid));
        sf.setStatus(resolved);
        if ("PAID".equalsIgnoreCase(resolved)) sf.setPaidAt(LocalDateTime.now());
        studentFeeRepo.save(sf);
    }
}
