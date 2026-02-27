-- V4: Create workflow/approval events table
CREATE TABLE approval_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    actor_user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    from_status item_status NOT NULL,
    to_status item_status NOT NULL,
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_approval_events_item_id ON approval_events(item_id);
CREATE INDEX idx_approval_events_actor_user_id ON approval_events(actor_user_id);
CREATE INDEX idx_approval_events_created_at ON approval_events(created_at DESC);

-- Create trigger to log workflow transitions
CREATE OR REPLACE FUNCTION log_item_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO approval_events (item_id, actor_user_id, from_status, to_status, comment)
        VALUES (NEW.id, NEW.updated_by_user_id, OLD.status, NEW.status, 'Status changed');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Note: We'll add the updated_by_user_id to items table tracking in application code
-- For now, approval events will be explicitly created through the workflow service
