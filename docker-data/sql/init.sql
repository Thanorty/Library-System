-- Create borrower table
CREATE TABLE borrower (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE
);

-- Create book table
CREATE TABLE book (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  isbn VARCHAR(17) NOT NULL,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  is_available BOOLEAN DEFAULT true
);

-- Create book_borrow table with corrected types for borrower_id and book_id
CREATE TABLE book_borrow (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  borrower_id BIGINT NOT NULL,  -- Fixed to BIGINT
  book_id BIGINT NOT NULL,      -- Fixed to BIGINT
  borrow_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  return_date TIMESTAMP,
  FOREIGN KEY (borrower_id) REFERENCES borrower(id) ON DELETE CASCADE,
  FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);
