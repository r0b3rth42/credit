package com.bootcamp.credit.controller;

import com.bootcamp.credit.business.CreditService;
import com.bootcamp.credit.dto.CreditRequestDTO;
import com.bootcamp.credit.dto.PaymentRequest;
import com.bootcamp.credit.model.Credit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @GetMapping
    public Flux<Credit> getAllCredits() {
        return creditService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Credit>> getCreditById(@PathVariable String id) {
        return creditService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public Flux<Credit> getCreditsByCustomerId(@PathVariable String customerId) {
        return creditService.findByCustomerId(customerId);
    }

    @GetMapping("/{id}/customer/{customerId}")
    public Mono<Boolean> findByIdAndCustomerId(@PathVariable String id, @PathVariable String customerId){
        return creditService.findByIdAndCustomerId(id, customerId)
                .defaultIfEmpty(false);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Credit> createCredit(@RequestBody Credit credit) {
        return creditService.create(credit);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Credit>> updateCredit(
            @PathVariable String id, @RequestBody Credit credit) {
        return creditService.update(id, credit)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/payment")
    public Mono<ResponseEntity<Credit>> makePayment(@RequestBody PaymentRequest request) {
        return creditService.makePayment(request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PostMapping("/{id}/consumption")
    public Mono<ResponseEntity<Credit>> chargeConsumption(
            @PathVariable String id, @RequestParam Double amount) {
        return creditService.chargeConsumption(id, amount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCredit(@PathVariable String id) {
        return creditService.delete(id);
    }

    @PostMapping("/{id}/transaction")
    public Mono<ResponseEntity<Credit>> transaction(@PathVariable String id, @RequestBody CreditRequestDTO dto){
        return creditService.transaction(id, dto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    //cliente no podra obtener un producto si posee alguna deuda vencida
    @GetMapping("/customer/{customerId}/debt")
    public Flux<Credit> hasDebt(@PathVariable  String customerId){
        return creditService.hasDebt(customerId);
    }
}
