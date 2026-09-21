package com.hulkhiretech.payment.repository.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.repository.interfaces.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionRepositoryImpl
        implements TransactionRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public int createTransaction(
            TransactionEntity entity) {

        log.info(
                "createTransaction called with entity: {}",
                entity
        );

        final String sql = """
                INSERT INTO payments.`transaction`
                (
                    user_id,
                    payment_method_id,
                    provider_id,
                    payment_type_id,
                    txn_status_id,
                    amount,
                    currency,
                    merchant_transaction_reference,
                    txn_reference,
                    provider_reference,
                    error_code,
                    error_message,
                    retry_count
                )
                VALUES
                (
                    :userId,
                    :paymentMethodId,
                    :providerId,
                    :paymentTypeId,
                    :txnStatusId,
                    :amount,
                    :currency,
                    :merchantTransactionReference,
                    :txnReference,
                    :providerReference,
                    :errorCode,
                    :errorMessage,
                    :retryCount
                )
                """;

        BeanPropertySqlParameterSource params =
                new BeanPropertySqlParameterSource(entity);

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        try {

            namedParameterJdbcTemplate.update(
                    sql,
                    params,
                    keyHolder,
                    new String[] { "id" }
            );

            Number key =
                    keyHolder.getKey();

            int generatedId =
                    key != null
                            ? key.intValue()
                            : -1;

            log.info(
                    "Transaction inserted with id: {}",
                    generatedId
            );

            return generatedId;

        } catch (Exception e) {

            log.error(
                    "Failed to insert transaction: {}",
                    e.getMessage(),
                    e
            );

            return -1;
        }
    }


    @Override
    public TransactionEntity getTxnByTxnReference(
            String txnReference) {

        log.info(
                "getTxnByTxnReference called with txnReference: {}",
                txnReference
        );

        final String sql = """
                SELECT
                    id,
                    user_id AS userId,
                    payment_method_id AS paymentMethodId,
                    provider_id AS providerId,
                    payment_type_id AS paymentTypeId,
                    txn_status_id AS txnStatusId,
                    amount,
                    currency,
                    merchant_transaction_reference
                        AS merchantTransactionReference,
                    txn_reference AS txnReference,
                    provider_reference AS providerReference,
                    error_code AS errorCode,
                    error_message AS errorMessage,
                    created_at AS createdAt,
                    updated_at AS updatedAt,
                    retry_count AS retryCount
                FROM payments.`transaction`
                WHERE txn_reference = :txnReference
                LIMIT 1
                """;

        MapSqlParameterSource params =
                new MapSqlParameterSource()
                        .addValue(
                                "txnReference",
                                txnReference
                        );

        try {

            return namedParameterJdbcTemplate.queryForObject(
                    sql,
                    params,
                    new BeanPropertyRowMapper<>(
                            TransactionEntity.class
                    )
            );

        } catch (EmptyResultDataAccessException e) {

            log.warn(
                    "No transaction found for txnReference: {}",
                    txnReference
            );

            return null;

        } catch (Exception e) {

            log.error(
                    "Error fetching transaction: {}",
                    e.getMessage(),
                    e
            );

            return null;
        }
    }


    @Override
    public boolean updateTxnDetails(
            TransactionEntity entity) {

        log.info(
                "updateTxnDetails called with entity: {}",
                entity
        );

        final String sql = """
                UPDATE payments.`transaction`
                SET
                    txn_status_id = :txnStatusId,
                    provider_reference = :providerReference,
                    error_code = :errorCode,
                    error_message = :errorMessage,
                    retry_count = :retryCount
                WHERE id = :id
                """;

        BeanPropertySqlParameterSource params =
                new BeanPropertySqlParameterSource(entity);

        try {

            int rows =
                    namedParameterJdbcTemplate.update(
                            sql,
                            params
                    );

            log.info(
                    "Updated {} transaction row(s)",
                    rows
            );

            return rows > 0;

        } catch (Exception e) {

            log.error(
                    "Failed to update transaction: {}",
                    e.getMessage(),
                    e
            );

            return false;
        }
    }
}