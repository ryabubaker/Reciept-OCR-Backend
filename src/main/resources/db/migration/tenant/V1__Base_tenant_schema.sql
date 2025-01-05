-- V1__init.sql

-- ==========================================
-- Enable Necessary Extensions
-- ==========================================

-- Enable pgcrypto extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";


-- 1. receipt_type Table
CREATE TABLE receipt_type (
                              receipt_type_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              name VARCHAR(255) NOT NULL,
                              template_path VARCHAR(1024) NOT NULL
);

CREATE TABLE receipt_type_fields (
                                     receipt_type_id UUID NOT NULL,
                                     column_name VARCHAR(255) NOT NULL,
                                     column_index INTEGER,
                                     PRIMARY KEY (receipt_type_id, column_name),
                                     FOREIGN KEY (receipt_type_id) REFERENCES receipt_type(receipt_type_id) ON DELETE CASCADE
);

-- 2. upload_requests Table
CREATE TABLE upload_requests (
                                 request_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                 uploaded_by_user_id UUID NOT NULL,
                                 uploaded_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                                 CONSTRAINT fk_upload_requests_uploaded_by_user
                                     FOREIGN KEY(uploaded_by_user_id)
                                         REFERENCES public.users(id)
                                         ON DELETE SET NULL
);

-- 3. receipt Table
CREATE TABLE receipt (
                         receipt_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         receipt_type_id VARCHAR(255) NOT NULL,
                         request_id UUID NOT NULL,
                         image_url VARCHAR(1024) NOT NULL,
                         status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                         ocr_data JSONB,
                         approved_by_user_id UUID,
                         approved_at TIMESTAMP WITHOUT TIME ZONE,
                         CONSTRAINT fk_receipt_receipt_type
                             FOREIGN KEY(receipt_type_id)
                                 REFERENCES receipt_type(receipt_type_id)
                                 ON DELETE CASCADE,
                         CONSTRAINT fk_receipt_upload_request
                             FOREIGN KEY(request_id)
                                 REFERENCES upload_requests(request_id)
                                 ON DELETE CASCADE,
                         CONSTRAINT fk_receipt_approved_by_user
                             FOREIGN KEY(approved_by_user_id)
                                 REFERENCES public.users(id)
                                 ON DELETE SET NULL
);

-- ==========================================
-- Create Indexes for Foreign Keys
-- ==========================================

-- Index for receipt_type_id
-- in receipt
CREATE INDEX idx_receipt_receipt_type_id ON receipt(receipt_type_id);

-- Index for request_id in receipt
CREATE INDEX idx_receipt_request_id ON receipt(request_id);

-- Index for approved_by_user_id in receipt
CREATE INDEX idx_receipt_approved_by_user_id ON receipt(approved_by_user_id);

-- Index for uploaded_by_user_id in upload_requests
CREATE INDEX idx_upload_requests_uploaded_by_user_id ON upload_requests(uploaded_by_user_id);

CREATE INDEX idx_receipt_type_fields_receipt_type_id ON receipt_type_fields (receipt_type_id);