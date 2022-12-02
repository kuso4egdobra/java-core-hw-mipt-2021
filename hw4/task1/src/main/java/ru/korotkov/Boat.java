package ru.korotkov;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Random;

@Data
@AllArgsConstructor
public final class Boat implements Comparable<Boat> {

    // Необходимо для использования PriorityBlockingQueue
    @Override
    public int compareTo(Boat o) {
        return this.toString().compareTo(o.toString());
    }

    enum Type {
        BREAD, BANANA, CLOTH;

        private static final List<Type> VALUES = List.of(values());
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Type randomType()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    enum Capacity {
        LOW(10), MEDIUM(50), LARGE(100);

        private final Integer capacity;

        Capacity(Integer capacity) {
            this.capacity = capacity;
        }

        public Integer getValue() {
            return this.capacity;
        }

        private static final List<Capacity> VALUES = List.of(values());
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Capacity randomCapacity()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    private Type type;
    private Capacity capacity;
}
