CREATE TABLE loans (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    borrowed_at TIMESTAMPTZ NOT NULL,
    due_date TIMESTAMPTZ NOT NULL,
    returned_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_loans_book
        FOREIGN KEY (book_id)
        REFERENCES books(id),

    CONSTRAINT fk_loans_member
        FOREIGN KEY (member_id)
        REFERENCES members(id),

    CONSTRAINT chk_loans_due_date
        CHECK (due_date >= borrowed_at),

    CONSTRAINT chk_loans_returned_at
        CHECK (
            returned_at IS NULL
            OR returned_at >= borrowed_at
        )
);