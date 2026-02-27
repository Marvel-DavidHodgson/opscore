-- V1: Create tenants table
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE industry_type AS ENUM ('ERP', 'EXPENSE', 'MANUFACTURING', 'LOGISTICS', 'SAP');

CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    primary_color VARCHAR(7) DEFAULT '#1e40af',
    industry_type industry_type NOT NULL,
    module_config JSONB DEFAULT '{}'::jsonb,
    label_overrides JSONB DEFAULT '{}'::jsonb,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tenants_slug ON tenants(slug);
CREATE INDEX idx_tenants_industry_type ON tenants(industry_type);

-- Insert a default tenant for development
INSERT INTO tenants (id, name, slug, industry_type, module_config, label_overrides)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Demo Corporation',
    'demo',
    'ERP',
    '{"modules": ["users", "items", "workflow", "reports", "audit"]}'::jsonb,
    '{"item": "業務項目", "items": "業務一覧"}'::jsonb
);
