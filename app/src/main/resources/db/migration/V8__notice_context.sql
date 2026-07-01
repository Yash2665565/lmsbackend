-- Add the `context` column used by Notice.repeat (additive, nullable).
ALTER TABLE company_briefings ADD COLUMN context VARCHAR(255) NULL;
