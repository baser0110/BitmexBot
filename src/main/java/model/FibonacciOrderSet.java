package model;

import lombok.Data;
import lombok.Getter;
import model.order_parameters.OrdType;
import model.order_parameters.Side;
import model.order_parameters.Symbol;
import util.CurrentMarkPrice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class FibonacciOrderSet {
    private int level;
    private double step;
    private double size;
    private List<Integer> fibSeq = new ArrayList<>();

    public FibonacciOrderSet(int level, double step, double size) {
        this.level = level;
        this.step = step;
        this.size = size;
        if (level <= 0 || step <= 0 || size <= 0) throw new IllegalArgumentException("Arguments must be positive!");
        if (step % 100 != 0) throw new IllegalArgumentException("The step must be a multiple of 100");
        switch (level) {
            case 1: fibSeq.add(1); break;
            case 2: fibSeq.add(1); fibSeq.add(1); break;
            default: fibSeq.add(1); fibSeq.add(1); fibSeq.add(1);
                for (int i = 3; i < level; i++) {
                    fibSeq.add(fibSeq.get(i-1) + fibSeq.get(i-2));
                } break;
        }
    }

    public List<BasicOrder> setFibOrders(Side side, double price) {
        List<BasicOrder> orders = new ArrayList<>();
        if (side == Side.Buy) {
            for (int i = 1; i <= level; i++) {
                orders.add(new OrderPostTest(Symbol.XBTUSD, Side.Buy, size * fibSeq.get(i - 1), Math.round(price) - getFibSum(i) * step, OrdType.Limit));
            }
        }
        else if (side == Side.Sell) {
            for (int i = 1; i <= level; i++) {
                orders.add(new OrderPostTest(Symbol.XBTUSD, Side.Sell, size * fibSeq.get(i - 1), Math.round(price) + getFibSum(i) * step, OrdType.Limit));
            }
        }

        for (BasicOrder o: orders
             ) {
            System.out.println(o);
        }

        return orders;
    }

    private int getFibSum(int currentLevel) {
        int result = 0;
        for (int i = 0; i < currentLevel; i++) {
            result += fibSeq.get(i);
        }
        return result;
    }


}
