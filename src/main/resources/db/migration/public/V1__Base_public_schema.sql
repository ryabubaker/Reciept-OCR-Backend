-- 1. Enable pgcrypto extension for UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 2. Create 'public' schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS public;

-- 3. Create 'roles' table
CREATE TABLE IF NOT EXISTS public.roles (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            name VARCHAR(30) NOT NULL UNIQUE
);

-- 4. Insert predefined roles
INSERT INTO public.roles (id, name)
SELECT gen_random_uuid(), 'ROLE_SYSTEM_ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM public.roles WHERE name = 'ROLE_SYSTEM_ADMIN'
);

INSERT INTO public.roles (id, name)
SELECT gen_random_uuid(), 'ROLE_COMPANY_ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM public.roles WHERE name = 'ROLE_COMPANY_ADMIN'
);

INSERT INTO public.roles (id, name)
SELECT gen_random_uuid(), 'ROLE_MOBILE_USER'
WHERE NOT EXISTS (
    SELECT 1 FROM public.roles WHERE name = 'ROLE_MOBILE_USER'
);

INSERT INTO public.roles (id, name)
SELECT gen_random_uuid(), 'ROLE_DESKTOP_USER'
WHERE NOT EXISTS (
    SELECT 1 FROM public.roles WHERE name = 'ROLE_DESKTOP_USER'
);

-- 5. Create 'tenants' table without 'admin_user_id' initially to avoid circular dependency
CREATE TABLE IF NOT EXISTS public.tenants (
                                              tenant_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              tenant_name VARCHAR(255) NOT NULL UNIQUE,
                                              status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                              created_at TIMESTAMP NOT NULL DEFAULT now(),
                                              updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- 6. Create 'users' table with a foreign key to 'tenants'
CREATE TABLE IF NOT EXISTS public.users (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            username VARCHAR(100) NOT NULL UNIQUE,
                                            password VARCHAR(255) NOT NULL,
                                            email VARCHAR(255) NOT NULL UNIQUE,
                                            email_verified BOOLEAN DEFAULT FALSE,
                                            image_url TEXT,
                                            phone_number VARCHAR(20),
                                            registered_provider_name VARCHAR(50),
                                            registered_provider_id VARCHAR(255),
                                            verification_code VARCHAR(255),
                                            verification_code_expires_at TIMESTAMP,
                                            tenant_id UUID,
                                            created_by_id UUID,
                                            created_date TIMESTAMP,
                                            last_modified_by_id UUID,
                                            last_modified_date TIMESTAMP,
                                            CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES public.tenants (tenant_id) ON DELETE CASCADE,
                                            CONSTRAINT fk_users_created_by FOREIGN KEY (created_by_id) REFERENCES public.users (id) ON DELETE SET NULL,
                                            CONSTRAINT fk_users_last_modified_by FOREIGN KEY (last_modified_by_id) REFERENCES public.users (id) ON DELETE SET NULL
);

-- 7. Create 'user_roles' join table
CREATE TABLE IF NOT EXISTS public.user_roles (
                                                 user_id UUID NOT NULL,
                                                 role_id UUID NOT NULL,
                                                 PRIMARY KEY (user_id, role_id),
                                                 CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES public.users (id) ON DELETE CASCADE,
                                                 CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES public.roles (id) ON DELETE RESTRICT
);


-- 8. Create 'admin_user_id' column in 'tenants' and add foreign key constraint
ALTER TABLE public.tenants
    ADD COLUMN admin_user_id UUID UNIQUE,
    ADD CONSTRAINT fk_tenants_admin_user FOREIGN KEY (admin_user_id) REFERENCES public.users (id) ON DELETE CASCADE;

-- 9. Indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_tenant_name ON public.tenants (tenant_name);
CREATE INDEX IF NOT EXISTS idx_admin_user_id ON public.tenants (admin_user_id);
CREATE INDEX IF NOT EXISTS idx_user_email ON public.users (email);
CREATE INDEX IF NOT EXISTS idx_user_username ON public.users (username);

-- 10. Enforce NOT NULL constraint on 'admin_user_id' if required
-- Uncomment the following line after ensuring all tenants have an associated admin user
-- ALTER TABLE public.tenants ALTER COLUMN admin_user_id SET NOT NULL;
