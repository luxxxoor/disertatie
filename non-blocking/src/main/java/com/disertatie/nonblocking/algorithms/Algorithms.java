package com.disertatie.nonblocking.algorithms;

import com.disertatie.nonblocking.tailCallOptimisation.annotations.TailRecursive;
import com.disertatie.nonblocking.tailCallOptimisation.annotations.TailRecursiveDirective;
import com.disertatie.nonblocking.tailCallOptimisation.annotations.TailRecursiveExecutor;

import java.math.BigInteger;

@TailRecursiveDirective(exportedAs = "Algo")
public class Algorithms {

    @TailRecursiveExecutor
    static public BigInteger fibonacci(int N) {
        if (N < 0) return BigInteger.valueOf(-1);
        if (N == 0 || N == 1)
            return BigInteger.valueOf(1L);
        return (BigInteger) _fibonacci(BigInteger.valueOf(1L), BigInteger.valueOf(1L), N - 2);
    }

    @TailRecursive
    static private Object _fibonacci(BigInteger prev, BigInteger current, int remainingIter) {
        if (remainingIter == 0)
            return current;
        return _fibonacci(current, prev.add(current), remainingIter - 1);
    }

    @TailRecursiveExecutor
    static public BigInteger ack(int m, int n) {
        if (m < 0 || n < 0) return BigInteger.valueOf(-1);
        return _ack(BigInteger.valueOf(m), BigInteger.valueOf(n));
    }

    @TailRecursive
    static private BigInteger _ack(BigInteger m, BigInteger n) {
        if (m.equals(BigInteger.ZERO)) {
            return n.add(BigInteger.ONE);
        } else if (n.equals(BigInteger.ZERO)) {
            return _ack(m.subtract(BigInteger.ONE), BigInteger.ONE);
        } else {
            return _ack(m.subtract(BigInteger.ONE), _ack(m, n.subtract(BigInteger.ONE)));
        }
    }
}