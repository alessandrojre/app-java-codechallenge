package com.yape.transaction.infrastructure.inbound.rest;

import com.yape.transaction.application.dto.CreateTransactionCommand;
import com.yape.transaction.application.dto.TransactionView;
import com.yape.transaction.application.usecase.CreateTransactionUseCase;
import com.yape.transaction.application.usecase.GetTransactionUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createUseCase;
    private final GetTransactionUseCase getUseCase;

    public TransactionController(CreateTransactionUseCase createUseCase,
                                 GetTransactionUseCase getUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> create(@Valid @RequestBody CreateTransactionRequest request) {

        UUID id = createUseCase.create(new CreateTransactionCommand(
                request.accountExternalIdDebit(),
                request.accountExternalIdCredit(),
                request.tranferTypeId(),
                request.value()
        ));

        return Map.of("transactionExternalId", id);
    }

    @GetMapping("/{transactionExternalId}")
    public TransactionResponse get(@PathVariable UUID transactionExternalId) {
        TransactionView view = getUseCase.getById(transactionExternalId);

        return new TransactionResponse(
                view.transactionExternalId(),
                new TransactionResponse.TransactionTypeResponse(view.transactionTypeName()),
                new TransactionResponse.TransactionStatusResponse(view.transactionStatusName()),
                view.value(),
                view.createdAt()
        );
    }
}
