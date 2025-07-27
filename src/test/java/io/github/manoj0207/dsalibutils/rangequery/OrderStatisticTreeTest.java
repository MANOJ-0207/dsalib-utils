package io.github.manoj0207.dsalibutils.rangequery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatisticTreeTest {

    private OrderStatisticTree<Integer> tree;

    @BeforeEach
    void setUp() {
        tree = new OrderStatisticTree<>();
    }

    @Test
    void testInsertAndCountBasic() {
        tree.insert(5);
        tree.insert(5);
        tree.insert(2);
        tree.insert(7);

        assertEquals(2, tree.count(5));
        assertEquals(1, tree.count(2));
        assertEquals(1, tree.count(7));
        assertEquals(0, tree.count(10));
    }

    @Test
    void testSizeWithDuplicates() {
        tree.insert(1);
        tree.insert(2);
        tree.insert(2);
        tree.insert(3);
        tree.insert(3);
        tree.insert(3);

        assertEquals(6, tree.size());
    }

    @Test
    void testOrderOfKey() {
        // Tree: [1, 2, 2, 4, 5]
        tree.insert(2);
        tree.insert(1);
        tree.insert(2);
        tree.insert(4);
        tree.insert(5);

        assertEquals(0, tree.orderOfKey(1));
        assertEquals(1, tree.orderOfKey(2));
        assertEquals(3, tree.orderOfKey(4));
        assertEquals(4, tree.orderOfKey(5));
        assertEquals(5, tree.orderOfKey(6));
    }

    @Test
    void testFindByOrder() {
        // Tree: [1, 2, 2, 3]
        tree.insert(2);
        tree.insert(1);
        tree.insert(2);
        tree.insert(3);

        assertEquals(1, tree.findByOrder(0));
        assertEquals(2, tree.findByOrder(1));
        assertEquals(2, tree.findByOrder(2));
        assertEquals(3, tree.findByOrder(3));
        assertNull(tree.findByOrder(-1));
        assertNull(tree.findByOrder(4));
    }

    @Test
    void testEraseSingleInstance() {
        tree.insert(2);
        tree.insert(1);
        tree.insert(2);
        tree.insert(3);

        tree.erase(2);  // One instance removed
        assertEquals(1, tree.count(2));
        assertEquals(3, tree.size());

        tree.erase(2);  // Last instance removed
        assertEquals(0, tree.count(2));
        assertEquals(2, tree.size());

        tree.erase(10); // Not in tree
        assertEquals(2, tree.size());
    }

    @Test
    void testEraseLeafAndRoot() {
        tree.insert(10);
        tree.insert(5);
        tree.insert(15);
        tree.insert(1);
        tree.insert(7);
        tree.insert(12);
        tree.insert(20);

        tree.erase(1); // Remove leaf
        assertEquals(0, tree.count(1));
        assertEquals(6, tree.size());

        tree.erase(10); // Remove root
        assertEquals(0, tree.count(10));
        assertEquals(5, tree.size());
    }

    @Test
    void testAllOperationsTogether() {
        tree.insert(10);
        tree.insert(20);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);

        assertEquals(6, tree.size());
        assertEquals(2, tree.count(20));
        assertEquals(3, tree.orderOfKey(30));

        assertEquals(20, tree.findByOrder(2));
        assertEquals(50, tree.findByOrder(5));
        assertNull(tree.findByOrder(6));

        tree.erase(20);
        assertEquals(1, tree.count(20));
        assertEquals(5, tree.size());

        tree.erase(20);
        assertEquals(0, tree.count(20));
        assertEquals(4, tree.size());
    }

    @Test
    void testEmptyTreeOperations() {
        assertEquals(0, tree.size());
        assertEquals(0, tree.count(10));
        assertEquals(0, tree.orderOfKey(10));
        assertNull(tree.findByOrder(0));
        tree.erase(10); // should not crash
    }

    @Test
    void testGenericStrings() {
        OrderStatisticTree<String> stringTree = new OrderStatisticTree<>();
        stringTree.insert("apple");
        stringTree.insert("banana");
        stringTree.insert("banana");
        stringTree.insert("cherry");

        assertEquals(2, stringTree.count("banana"));
        assertEquals(1, stringTree.count("apple"));
        assertEquals(0, stringTree.count("date"));
        assertEquals(1, stringTree.orderOfKey("banana"));
        assertEquals("banana", stringTree.findByOrder(2));
    }

    @Test
    void testCustomObject() {
        class Person implements Comparable<Person> {
            String name;
            int age;

            Person(String name, int age) {
                this.name = name;
                this.age = age;
            }

            @Override
            public int compareTo(Person o) {
                return Integer.compare(this.age, o.age);
            }

            @Override
            public String toString() {
                return name + "(" + age + ")";
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Person && ((Person) obj).age == age && ((Person) obj).name.equals(name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(name, age);
            }
        }

        OrderStatisticTree<Person> people = new OrderStatisticTree<>();
        Person a = new Person("A", 30);
        Person a2 = new Person("A", 30);
        Person b = new Person("B", 25);
        Person c = new Person("C", 40);

        people.insert(a);
        people.insert(b);
        people.insert(a2);
        people.insert(c);

        assertEquals(2, people.count(a));
        assertEquals(1, people.orderOfKey(a)); // b is less than a
        assertEquals(a, people.findByOrder(2));
    }
}
