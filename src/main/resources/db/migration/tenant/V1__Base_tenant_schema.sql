
CREATE TABLE IF NOT EXISTS receipt_type (
                                            receipt_type_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            name VARCHAR(255) NOT NULL UNIQUE,
                                            description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS receipt_type_fields (
                                                   receipt_type_id UUID NOT NULL,
                                                   field_name VARCHAR(255) NOT NULL,
                                                   field_type VARCHAR(50) NOT NULL,
                                                   PRIMARY KEY (receipt_type_id, field_name),
                                                   CONSTRAINT fk_receipt_type_fields_receipt_type FOREIGN KEY (receipt_type_id) REFERENCES receipt_type (receipt_type_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS upload_requests (
                                               request_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                               status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                               uploaded_by_user_id UUID NOT NULL,
                                               uploaded_at TIMESTAMP NOT NULL DEFAULT now(),
                                               CONSTRAINT fk_upload_requests_uploaded_by FOREIGN KEY (uploaded_by_user_id) REFERENCES public.users (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS receipt (
                                       receipt_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       request_id UUID,
                                       receipt_type_id UUID NOT NULL,
                                       image_url TEXT NOT NULL,
                                       status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                       ocr_data JSONB,
                                       approved_by_user_id UUID,
                                       approved_at TIMESTAMP,
                                       CONSTRAINT fk_receipt_request FOREIGN KEY (request_id) REFERENCES upload_requests (request_id) ON  DELETE CASCADE,
                                       CONSTRAINT fk_receipt_receipt_type FOREIGN KEY (receipt_type_id) REFERENCES receipt_type (receipt_type_id) ON DELETE RESTRICT,
                                       CONSTRAINT fk_receipt_approved_by FOREIGN KEY (approved_by_user_id) REFERENCES public.users (id) ON DELETE SET NULL
);


CREATE INDEX IF NOT EXISTS idx_upload_requests_status ON upload_requests (status);
CREATE INDEX IF NOT EXISTS idx_receipt_type_name ON receipt_type (name);
CREATE INDEX IF NOT EXISTS idx_receipt_status ON receipt (status);
CREATE INDEX IF NOT EXISTS idx_receipt_receipt_type_id ON receipt (receipt_type_id);