package com.library.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Issue {
    public static final int FINE_PER_DAY = 5;   // Rs. per overdue day
    public static final int LOAN_DAYS = 14;

    private final int id, bookId, fine;
    private final String bookTitle, memberName;
    private final Date issueDate, dueDate, returnDate;

    public Issue(int id, int bookId, String bookTitle, String memberName, Date issueDate, Date dueDate, Date returnDate, int fine) {
        this.id = id; this.bookId = bookId; this.bookTitle = bookTitle; this.memberName = memberName;
        this.issueDate = issueDate; this.dueDate = dueDate; this.returnDate = returnDate; this.fine = fine;
    }
    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getMemberName() { return memberName; }
    public Date getIssueDate() { return issueDate; }
    public Date getDueDate() { return dueDate; }
    public Date getReturnDate() { return returnDate; }
    public boolean isReturned() { return returnDate != null; }
    public long getLateDays() {
        if (returnDate != null) return fine / FINE_PER_DAY;
        return Math.max(0, ChronoUnit.DAYS.between(dueDate.toLocalDate(), LocalDate.now()));
    }
    public boolean isOverdue() { return returnDate == null && getLateDays() > 0; }
    /** Final fine if returned, running fine if still out. */
    public long getCurrentFine() { return returnDate != null ? fine : getLateDays() * FINE_PER_DAY; }
}
