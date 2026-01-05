package com.yape.transaction.infrastructure.inbound.rest.controller;

import com.yape.transaction.application.dto.CreateTransactionCommand;
import com.yape.transaction.application.usecase.CreateTransactionUseCase;
import com.yape.transaction.application.usecase.GetTransactionUseCase;
import com.yape.transaction.infrastructure.inbound.rest.dto.CreateTransactionRequest;
import com.yape.transaction.infrastructure.inbound.rest.dto.TransactionResponse;
import com.yape.transaction.infrastructure.inbound.rest.mapper.TransactionMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/transactions")
public class TransactionController {
    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final TransactionMapper transactionMapper;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                 TransactionMapper transactionMapper,
                                 GetTransactionUseCase getTransactionUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.transactionMapper = transactionMapper;
        this.getTransactionUseCase = getTransactionUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        return createTransactionUseCase.create(new CreateTransactionCommand(
                request.transactionExternalId(),
                request.accountExternalIdDebit(),
                request.accountExternalIdCredit(),
                request.transferTypeId(),
                request.value()
        )).map(transactionMapper::toResponse);
    }

    @GetMapping("/{id}")
    public Mono<TransactionResponse> getById(@PathVariable UUID id) {
        return getTransactionUseCase.getById(id)
                .map(view -> new TransactionResponse(
                        view.transactionExternalId(),
                        new TransactionResponse.TransactionTypeResponse(view.transactionTypeName()),
                        new TransactionResponse.TransactionStatusResponse(view.transactionStatusName()),
                        view.value(),
                        view.createdAt()
                ));
    }

}