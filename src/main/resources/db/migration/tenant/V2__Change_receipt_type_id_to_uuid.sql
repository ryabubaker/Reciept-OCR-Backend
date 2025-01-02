-- V2__alter_receipt_type_id_to_uuid.sql

BEGIN;

-- 1. Drop Foreign Key Constraints Referencing receipt_type_id in receipt Table
ALTER TABLE receipt DROP CONSTRAINT IF EXISTS fk_receipt_receipt_type;

-- 2. Alter receipt_type_id Column in receipt_type Table to UUID
ALTER TABLE receipt_type
ALTER COLUMN receipt_type_id TYPE UUID USING receipt_type_id::uuid,
    ALTER COLUMN receipt_type_id SET DEFAULT gen_random_uuid();

-- 3. Alter receipt_type_id Column in receipt Table to UUID
ALTER TABLE receipt
ALTER COLUMN receipt_type_id TYPE UUID USING receipt_type_id::uuid;

-- 4. Recreate Foreign Key Constraints in receipt Table
ALTER TABLE receipt
    ADD CONSTRAINT fk_receipt_receipt_type
        FOREIGN KEY(receipt_type_id)
            REFERENCES receipt_type(receipt_type_id)
            ON DELETE CASCADE;

-- 5. Update Indexes (If Necessary)
-- Drop existing index on receipt_type_id if it's incompatible
DROP INDEX IF EXISTS idx_receipt_receipt_type_id;

-- Recreate the index with the correct data type
CREATE INDEX idx_receipt_receipt_type_id ON receipt(receipt_type_id);

COMMIT;
