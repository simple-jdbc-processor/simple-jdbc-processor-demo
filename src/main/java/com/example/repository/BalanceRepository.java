package com.example.repository;

import com.example.domain.Balance;
import com.example.domain.BalanceExample;
import com.example.domain.BalanceSimpleJdbcRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class BalanceRepository extends BalanceSimpleJdbcRepository {

    /**
     * sql增加余额.
     */
    public int increment(Long id, BigDecimal amount) {
        String sql = "update tb_balance set balance = balance + ? where id = ?";
        return update(sql, Arrays.asList(amount, id));
    }

    /**
     * example 冻结余额.
     */
    public int frozen(Long id, BigDecimal amount) {
        BalanceExample query = BalanceExample.create()
                .set("balance = balance - ?", amount)
                .set("frozen = frozen + ?", amount)
                .andIdEqualTo(id);
        return updateByExampleSelective(query);
    }

    public int[] batchIncrement(List<Balance> balances) {
        String sql = "update tb_balance set balance = balance + ? where id = ?";
        List<Object[]> params = new ArrayList<>();
        for (Balance balance : balances) {
            params.add(new Object[]{balance.getBalance(), balance.getId()});
        }
        return updateBatch(sql, params);
    }

    public int[] batchFrozen(List<Balance> balances) {
        String sql = "update tb_balance set balance = balance - ? where id = ?";
        List<Object[]> params = new ArrayList<>();
        for (Balance balance : balances) {
            params.add(new Object[]{balance.getBalance(), balance.getId()});
        }
        return updateBatch(sql, params);
    }

    public List<Balance> sumBalance(Long userId) {
        BalanceExample query = BalanceExample.create()
                .aggregate("id", "sum(balance) as c", "sum(frozen)")
                .andIdEqualTo(userId)
                .groupBy(BalanceExample.Column.id)
                .having("c > 1")
                .limit(10);
        return aggregate(query, rs -> {
            Long id = rs.getLong(1);
            BigDecimal balance = rs.getBigDecimal(2);
            BigDecimal frozen = rs.getBigDecimal(3);
            return new Balance()
                    .setId(id)
                    .setBalance(balance == null ? BigDecimal.ZERO : balance)
                    .setFrozen(frozen == null ? BigDecimal.ZERO : frozen);
        });
    }
}
