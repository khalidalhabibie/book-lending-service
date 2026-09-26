CREATE INDEX idx_loans_member_active
    ON loans(member_id)
    WHERE returned_at IS NULL;

CREATE INDEX idx_loans_member_due_date_active
    ON loans(member_id, due_date)
    WHERE returned_at IS NULL;

CREATE INDEX idx_loans_book_active
    ON loans(book_id)
    WHERE returned_at IS NULL;