-- V3: Create items table
CREATE TYPE item_status AS ENUM ('DRAFT', 'ACTIVE', 'PENDING', 'APPROVED', 'REJECTED', 'CLOSED');

CREATE TABLE items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    status item_status NOT NULL DEFAULT 'DRAFT',
    assigned_to_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_by_user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_items_tenant_code UNIQUE (tenant_id, code)
);

CREATE INDEX idx_items_tenant_id ON items(tenant_id);
CREATE INDEX idx_items_status ON items(status);
CREATE INDEX idx_items_category ON items(category);
CREATE INDEX idx_items_assigned_to ON items(assigned_to_user_id);
CREATE INDEX idx_items_created_by ON items(created_by_user_id);
CREATE INDEX idx_items_created_at ON items(created_at DESC);

-- Create sequence for item code generation per tenant
CREATE SEQUENCE IF NOT EXISTS item_code_seq START 1000;

-- Function to generate item codes
CREATE OR REPLACE FUNCTION generate_item_code(tenant_prefix TEXT)
RETURNS TEXT AS $$
DECLARE
    next_val INTEGER;
BEGIN
    next_val := nextval('item_code_seq');
    RETURN tenant_prefix || '-' || LPAD(next_val::TEXT, 6, '0');
END;
$$ LANGUAGE plpgsql;
