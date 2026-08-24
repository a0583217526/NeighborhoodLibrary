package Book.and.Loaning.Management.System.Controller;

import Book.and.Loaning.Management.System.DTO.BookDTO;
import Book.and.Loaning.Management.System.DTO.LoanDTO;
import Book.and.Loaning.Management.System.Entity.LoanEntity;
import Book.and.Loaning.Management.System.Services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PostMapping("/requestLoan")
    public ResponseEntity<LoanDTO> requestLoan(@RequestBody LoanDTO loanDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(loanDto));
    }

    @PatchMapping("/update")
    public ResponseEntity<LoanDTO> updateLoan(@RequestBody LoanDTO loanDto) {
        return ResponseEntity.ok(loanService.updateLoan(loanDto));
    }

    @DeleteMapping("/deleteLoan/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable UUID id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findLoansByOwnerId/{ownerId}")
    public ResponseEntity<List<LoanDTO>> findLoansByOwnerId(@PathVariable UUID ownerId) {
        List<LoanDTO> loans = loanService.findLoansByOwnerId(ownerId);
        return ResponseEntity.ok(loans);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<LoanEntity> approveLoan(@PathVariable UUID id, @RequestHeader("userId") UUID currentUserId) {
        return ResponseEntity.ok(loanService.approveLoan(id, currentUserId));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<LoanEntity> rejectLoan(@PathVariable UUID id, @RequestHeader("userId") UUID currentUserId) {
        return ResponseEntity.ok(loanService.rejectLoan(id, currentUserId));
    }

    @GetMapping("/pending-requests/{ownerId}")
    public ResponseEntity<List<LoanDTO>> getPendingRequests(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(loanService.getPendingRequestsByOwnerId(ownerId));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanDTO> returnBook(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.returnBook(id));
    }

    @GetMapping("/getBorrowedBooksByOwnerId/{ownerId}")
    public ResponseEntity<List<BookDTO>> getBorrowedBooksByOwnerId(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(loanService.getBorrowedBooksByOwnerId(ownerId));
    }
    //פונקציה להארכת זמן
    @PatchMapping("/timeExtension/{id}")
    public ResponseEntity<LoanDTO> timeExtension(@PathVariable UUID id, @RequestParam int loanDurationDays) {
        return ResponseEntity.ok(loanService.timeExtension(id, loanDurationDays));
    }
}