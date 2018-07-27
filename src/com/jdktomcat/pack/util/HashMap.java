package com.jdktomcat.pack.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;


/**
 * HashTable 实现Map接口。但是这个实现提供了所有map操作，而且允许null值与null键值。（
 * HashMap类基本上与Hashtable功能类似，除了允许null值。）这个类不能保证map的顺序，尤其
 * 不能保证顺序是不变的。
 * <p>
 * 该实现类基本操作是常量时间（n）性能消耗（get、put），假装hash函数分散元素正确的槽点。
 * 遍历集合所需时间和其HashMap实例容量（槽点数量）+ 其Entry数量成比例的，那么，为确保遍历
 * 性能，最好不要将capacity设置的过高（或者负载因子过低）。
 * <p>
 * HashMap主要有两个参数影响到其性能：初始容量与负载因子。
 * 初始容量：槽点数量，初始容量只是在hash table创建的时候创建的。
 * 负载因子：是一种度量（hash tbale有多满在他的容量自动扩容之前）。
 * 当hash table中entry数量超过负载因子与容量，hash table就会重新hash（
 * 那就是说，内部的数据结构会被重新创建），那么 hash table拥有大约两倍的槽点数量。
 * <p>
 * 通用的规则，默认负载因子为（0.75）提供一个在时间与空间消耗上消耗平衡。这个数值越大，消耗空间越小，而时间消耗反而越大
 * （反应到HashMap大部分操作，get、put）。你想要存储键值对数量和负载因子应该考虑到初始化槽点容量设置，这样子才能够尽量
 * 减少rehash操作。如果初始化槽点数量比最大键值对数量除以负载因子，将没有rehash操作。
 * <p>
 * 如果想要HashMap存储很多键值对的话，应该在创建HashMap指定充足的槽点数量，这样子比让HashMap
 * 自动扩容效率更高。
 * <p>
 * 需要注意的是：HashMap不是同步的，如果多个线程同时访问hash map的话，至少一个线程修改
 * map的结构，他必须从外部同步。（修改结构：添加、删除映射。几乎不会修改值关联一个key）。
 * 这个是典型完成同步利用自然的包裹map
 * <p>
 * 如果没有这样的对象存在的话，map可以用Collections.synchronizedMap包装方法包装一下。最好
 * 在创建的时候就包装一下，以防不确定不同步访问。
 * 示例：Map m = Collections.synchronizedMap(new HashMap(...));
 * <p>
 * 遍历集合方法返回迭代器是快速失败的：如果map在迭代器创建之后发生结构变化时，除了迭代器自身删除方法，
 * 迭代器都会抛出ConcurrentModificationException异常。那么，面对同步修改时，迭代器会直接失败清除，
 * 而不会冒武断的、不确定的行为在未决定的时间。
 * <p>
 * 注意：迭代器快速失败行为不能保证如他名称所说，也就是说，不可能完全保证持久化非同步化的
 * 同时发生的修改。快速失败迭代器抛出ConcurrentModificationException异常
 * 在最大努力基础上。因此，程序依赖这个异常来保证map的正确性是错误的，迭代器快速失败
 * 行为只能被用来探测bug。
 * <p>
 * 该类为JDK基础集合类。
 *
 * @param <K> the type of keys maintained by this map 键
 * @param <V> the type of mapped values 值
 * @author 汤旗
 * @see Hashtable
 * @see Object#hashCode()
 * @see Collection
 * @see Map
 * @see TreeMap
 * @since 1.2
 */
public class HashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {

    /**
     * 默认初始化容量：必须是2的幂次方
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * 最大容量，被用来如果一个指定毫无限制大值参数。必须为2的幂次方 1<<30也即是2的30次方。
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * 默认负载因子（0.75）
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * map键值对表，需要的话会重新扩展。长度必须为2的幂次方。
     * <p>
     * （被声明为transient的属性不会被序列化，这就是transient关键字的作用）
     */
    private transient HashMap.Entry[] table;

    /**
     * map中键值对数量
     */
    private transient int size;

    /**
     * 下一次扩容大小
     */
    private int threshold;

    /**
     * 哈希表负载因子
     */
    private final float loadFactor;

    /**
     * HashMap发生结构改变的次数，结构改变就是指键值对数量改变或者内部结构修改。
     * 这个属性被用来迭代器快速失败。
     * transient的属性不会被序列化，volatile线程可见性
     */
    private transient volatile int modCount;

    /**
     * 构造器：空值 指定容量和负载因子
     *
     * @param initialCapacity 指定容量
     * @param loadFactor      负载因子
     * @throws IllegalArgumentException 容量、负载因子为负值的情况下
     */
    public HashMap(int initialCapacity, float loadFactor) {
        // 校验参数有效性
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        this.loadFactor = loadFactor;
        threshold = (int) (capacity * loadFactor);
        table = new HashMap.Entry[capacity];
        init();
    }

    /**
     * 构造器：空值 指定容量、默认负载因子0.75
     *
     * @param initialCapacity 指定容量.
     * @throws IllegalArgumentException 指定容量为负值情况下.
     */
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 构造器：空值 默认容量16、默认负载因子0.75
     */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new HashMap.Entry[DEFAULT_INITIAL_CAPACITY];
        init();
    }

    /**
     * 构造器：根据指定Map创建新的HashMap，负载因子为0.75，初始容量能够装下指定Map的键值对。
     *
     * @param m 指定Map
     * @throws NullPointerException 如果指定Map为空的情况下
     */
    public HashMap(Map<? extends K, ? extends V> m) {
        // 容量 初始map的键值对大小除以默认的负载因子+1 默认容量比较大小取大者
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        // 将原始数据放到新map中
        putAllForCreate(m);
    }


    /**
     * 子类初始化钩子。这个方法被调用在所有的构造器和伪构造器HashMap被初始化完成之后，在
     * 键值对插入之前。（这个方法缺失的话，readObject方法就会明确要求子类知道这个方法。）
     */
    void init() {
    }

    /**
     * 适用一个对一个给定哈希编码补充哈希方法，它可以提升哈希方法运行效率低的问题。这是相当要紧
     * 的，因为HashMap适用2的幂次方长度哈希表，除此以外，遇到哈希编码碰撞的时候会有一些不同。
     * 提示：Null永远映射到hash 0，索引0.
     *
     * @param h 哈希编码
     */
    static int hash(int h) {
        // 这个方法确保哈希编码不同一致多样性在每一个位置有一个碰撞边界值。
        // 默认负载因子情况下大约8个
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * 返回哈希码索引(也就是h取余length)
     *
     * @param h      哈希码
     * @param length 长度
     */
    static int indexFor(int h, int length) {
        return h & (length - 1);
    }

    /**
     * 返回键值对数量
     *
     * @return 键值对数量
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * 判空
     *
     * @return 是否为空
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 返回指定键值映射值，或者为空（如果该键值没有映射，也有可能有映射，映射就是null两种情况）
     *
     * @param key 键
     * @see #put(Object, Object)
     */
    @Override
    public V get(Object key) {
        // 若果键为null的话，返回对应的映射
        if (key == null) {
            return getForNullKey();
        }
        // 计算哈希
        int hash = hash(key.hashCode());
        // 先查找到对应的槽索引，找到对应的键值对
        for (HashMap.Entry<K, V> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
            Object k = e.key;
            boolean isExist = e.hash == hash && (k == key || key.equals(k));
            if (isExist) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * Offloaded version of get() to look up null keys.  Null keys map
     * to index 0.  This null case is split out into separate methods
     * for the sake of performance in the two most commonly used
     * operations (get and put), but incorporated with conditionals in
     * others.
     */
    private V getForNullKey() {
        for (HashMap.Entry<K, V> e = table[0]; e != null; e = e.next) {
            if (e.key == null) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    @Override
    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    /**
     * Returns the entry associated with the specified key in the
     * HashMap.  Returns null if the HashMap contains no mapping
     * for the key.
     */
    final HashMap.Entry<K, V> getEntry(Object key) {
        int hash = (key == null) ? 0 : hash(key.hashCode());
        for (HashMap.Entry<K, V> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) {
                return e;
            }
        }
        return null;
    }


    /**
     * 联合指定值与指定键在map中，如果map中键之前已经存在映射关系，新的值将覆盖。
     *
     * @param key   关联键
     * @param value 关联值
     * @return 原先映射值，如果不存在，将返回null
     */
    @Override
    public V put(K key, V value) {
        // 判断键是否为空
        if (key == null) {
            // 设置null键值
            return putForNullKey(value);
        }
        // 计算哈希
        int hash = hash(key.hashCode());
        // 计算槽索引
        int i = indexFor(hash, table.length);
        // 查找对应键记录
        for (HashMap.Entry<K, V> e = table[i]; e != null; e = e.next) {
            Object k = e.key;
            if (e.hash == hash && (k == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        // 如果没有，则修改结构次数增加
        modCount++;
        // 添加记录
        addEntry(hash, key, value, i);
        // 返回空
        return null;
    }

    /**
     * Offloaded version of put for null keys
     */
    private V putForNullKey(V value) {
        for (HashMap.Entry<K, V> e = table[0]; e != null; e = e.next) {
            if (e.key == null) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        modCount++;
        addEntry(0, null, value, 0);
        return null;
    }

    /**
     * 在构造器或者伪构造器（复制、读）中，这个方法被用来替代put方法。这个方法不会重新归置hash表、检查同步修改之类的操作。
     * 它调用createEntry而不是addEntry
     */
    private void putForCreate(K key, V value) {
        // 计算key的哈希值 key为null时hash为0 不为空则为计算hash
        int hash = (key == null) ? 0 : hash(key.hashCode());
        // 计算索引值
        int i = indexFor(hash, table.length);
        /**
         * 查找已经存在key键值对。这个永远不会发生在复制或者反序列化过程中，它只会发生在根据输入
         * 顺序不一致map构造函数中。
         */
        for (HashMap.Entry<K, V> e = table[i]; e != null; e = e.next) {
            K k = e.key;
            // 判断是否已经存在相同key
            boolean isExist = (e.hash == hash && (k == key || (key != null && key.equals(k))));
            if (isExist) {
                // 修改对应键值对值
                e.value = value;
                // 直接返回
                return;
            }
        }
        // 创建键值对
        createEntry(hash, key, value, i);
    }

    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.  This method is called automatically when the
     * number of keys in this map reaches its threshold.
     * <p>
     * If current capacity is MAXIMUM_CAPACITY, this method does not
     * resize the map, but sets threshold to Integer.MAX_VALUE.
     * This has the effect of preventing future calls.
     * <p>
     * 重置该map哈希内容到新的大容量数组。这个方法将被自动调用当map容量达到threshold设定值时。
     * 如果现在容量是最大容量时，这个方法不会重置map，会设置threshold为整形最大值，这样子就会
     * 防止以后再执行该方法。
     *
     * @param newCapacity 新容量，必须是2的幂次方，一定要大于现在容量（除非是已经到了最大容量，以防无效参数）
     */
    void resize(int newCapacity) {
        // 原先键值对数组
        HashMap.Entry[] oldTable = table;
        // 原先长度
        int oldCapacity = oldTable.length;
        // 判断是否已经达到最大容量
        if (oldCapacity == MAXIMUM_CAPACITY) {
            // 阈值
            threshold = Integer.MAX_VALUE;
            return;
        }
        // 创建新键值对数组
        HashMap.Entry[] newTable = new HashMap.Entry[newCapacity];
        // 将老数据转移到新键值对数组上
        transfer(newTable);
        // 指定为新键值对数组
        table = newTable;
        // 设置下次重置阈值
        threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * 将原始数据放到新的map中
     *
     * @param m 原始map
     */
    private void putAllForCreate(Map<? extends K, ? extends V> m) {
        // 迭代器遍历
        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) {
            // 键值对
            Map.Entry<? extends K, ? extends V> e = i.next();
            // 放到新map中
            putForCreate(e.getKey(), e.getValue());
        }
    }

    /**
     * 将所有记录转移到新表
     *
     * @param newTable 新表
     */
    void transfer(HashMap.Entry[] newTable) {
        // 旧表
        HashMap.Entry[] src = table;
        // 新长度
        int newCapacity = newTable.length;
        // 遍历老表记录
        for (int j = 0; j < src.length; j++) {
            // 记录
            HashMap.Entry<K, V> e = src[j];
            // 判空
            if (e != null) {
                // 将老表位置置空
                src[j] = null;
                do {
                    // 获取下一个记录
                    HashMap.Entry<K, V> next = e.next;
                    // 计算在新表中桶索引
                    int i = indexFor(e.hash, newCapacity);
                    // 设置记录下一个记录为原先桶索引处记录
                    e.next = newTable[i];
                    // 然后将记录放置到桶索引处
                    newTable[i] = e;
                    // 传递到原先下一个记录
                    e = next;
                    // 直到记录为空
                } while (e != null);
            }
        }
    }

    /**
     * 复制所有的映射到本map中，这些映射将覆盖本map中的键值对。
     *
     * @param m 目标map
     * @throws NullPointerException 空指针异常，当m
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        // 要被添加的键数量
        int numKeysToBeAdded = m.size();
        // 如果数量为空的话，直接返回
        if (numKeysToBeAdded == 0) {
            return;
        }

        /*
         * Expand the map if the map if the number of mappings to be added
         * is greater than or equal to threshold.  This is conservative; the
         * obvious condition is (m.size() + size) >= threshold, but this
         * condition could result in a map with twice the appropriate capacity,
         * if the keys to be added overlap with the keys already in this map.
         * By using the conservative calculation, we subject ourself
         * to at most one extra resize.
         * 扩展map如果map的数量如果达到threshold（阈值），这是保守的。明显的条件是（m的大小加上现大小不小于
         * 阈值，但是这个条件会导致）
         *
         */
        if (numKeysToBeAdded > threshold) {
            int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY) {
                targetCapacity = MAXIMUM_CAPACITY;
            }
            int newCapacity = table.length;
            while (newCapacity < targetCapacity) {
                newCapacity <<= 1;
            }
            if (newCapacity > table.length) {
                resize(newCapacity);
            }
        }

        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<? extends K, ? extends V> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * (A <tt>null</tt> return can also indicate that the map
     * previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    @Override
    public V remove(Object key) {
        HashMap.Entry<K, V> e = removeEntryForKey(key);
        return (e == null ? null : e.value);
    }

    /**
     * Removes and returns the entry associated with the specified key
     * in the HashMap.  Returns null if the HashMap contains no mapping
     * for this key.
     */
    final HashMap.Entry<K, V> removeEntryForKey(Object key) {
        int hash = (key == null) ? 0 : hash(key.hashCode());
        int i = indexFor(hash, table.length);
        HashMap.Entry<K, V> prev = table[i];
        HashMap.Entry<K, V> e = prev;

        while (e != null) {
            HashMap.Entry<K, V> next = e.next;
            Object k;
            if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) {
                modCount++;
                size--;
                if (prev == e) {
                    table[i] = next;
                } else {
                    prev.next = next;
                }
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * Special version of remove for EntrySet.
     */
    final HashMap.Entry<K, V> removeMapping(Object o) {
        if (!(o instanceof Map.Entry)) {
            return null;
        }

        Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
        Object key = entry.getKey();
        int hash = (key == null) ? 0 : hash(key.hashCode());
        int i = indexFor(hash, table.length);
        HashMap.Entry<K, V> prev = table[i];
        HashMap.Entry<K, V> e = prev;

        while (e != null) {
            HashMap.Entry<K, V> next = e.next;
            if (e.hash == hash && e.equals(entry)) {
                modCount++;
                size--;
                if (prev == e) {
                    table[i] = next;
                } else {
                    prev.next = next;
                }
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }
        return e;
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    @Override
    public void clear() {
        modCount++;
        HashMap.Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            tab[i] = null;
        }
        size = 0;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     * specified value
     */
    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return containsNullValue();
        }
        HashMap.Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (HashMap.Entry e = tab[i]; e != null; e = e.next) {
                if (value.equals(e.value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Special-case code for containsValue with null argument
     */
    private boolean containsNullValue() {
        HashMap.Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (HashMap.Entry e = tab[i]; e != null; e = e.next) {
                if (e.value == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
     * values themselves are not cloned.
     *
     * @return a shallow copy of this map
     */
    @Override
    public Object clone() {
        HashMap<K, V> result = null;
        try {
            result = (HashMap<K, V>) super.clone();
        } catch (CloneNotSupportedException e) {
            // assert false;
        }
        result.table = new HashMap.Entry[table.length];
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.init();
        result.putAllForCreate(this);

        return result;
    }

    /**
     * 键值对对象
     *
     * @param <K> 键
     * @param <V> 值
     */
    static class Entry<K, V> implements Map.Entry<K, V> {
        /**
         * 键名
         */
        final K key;
        /**
         * 值
         */
        V value;
        /**
         * 下一个键值对
         */
        HashMap.Entry<K, V> next;
        /**
         * 哈希值
         */
        final int hash;

        /**
         * Creates new entry.
         */
        Entry(int h, K k, V v, HashMap.Entry<K, V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }

        @Override
        public final K getKey() {
            return key;
        }

        @Override
        public final V getValue() {
            return value;
        }

        @Override
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        /**
         * 判等
         *
         * @param o 对象
         * @return 判等
         */
        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry) o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            boolean checkKeyEqual = k1 == k2 || (k1 != null && k1.equals(k2));
            if (checkKeyEqual) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                boolean checkValueEqual = v1 == v2 || (v1 != null && v1.equals(v2));
                if (checkValueEqual) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public final int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        @Override
        public final String toString() {
            return getKey() + "=" + getValue();
        }

        /**
         * 这个方法被调用在任意时刻调用put方法时，当key已经存在时。
         */
        void recordAccess(HashMap<K, V> m) {
        }

        /**
         * 这个方法被调用在任意时刻调用remove方法时。
         */
        void recordRemoval(HashMap<K, V> m) {
        }
    }

    /**
     * 添加一个新的指定键、值、哈希码、指定桶记录。这个方法有责任在满足条件重置表。
     * 子类重写该方法来修改put方法的操作行为。
     */
    void addEntry(int hash, K key, V value, int bucketIndex) {
        // 获取原先索引桶
        HashMap.Entry<K, V> e = table[bucketIndex];
        // 将该索引处记录设置为新增记录
        table[bucketIndex] = new HashMap.Entry<K, V>(hash, key, value, e);
        // 如果表大小达到重置大小条件的话，需要重置大小
        if (size++ >= threshold) {
            // 重置大小为原先表长度的两倍
            resize(2 * table.length);
        }
    }

    /**
     * 就像addEntry除了这个版本被使用当创建映射表作为map构造器（伪构造：克隆反序列化）。这个方法
     * 不用担心重置table。子类重写该方法当克隆与读时。
     *
     * @param hash        哈希
     * @param key         键值
     * @param value       值
     * @param bucketIndex 槽点索引
     */
    void createEntry(int hash, K key, V value, int bucketIndex) {
        // 获取槽点键值对象
        HashMap.Entry<K, V> e = table[bucketIndex];
        // 创建键值对并赋值到对应的槽点位置
        table[bucketIndex] = new HashMap.Entry<K, V>(hash, key, value, e);
        // 大小加1
        size++;
    }

    private abstract class HashIterator<E> implements Iterator<E> {
        /**
         * next entry to return
         */
        HashMap.Entry<K, V> next;
        /**
         * For fast-fail
         */
        int expectedModCount;
        /**
         * current slot
         */
        int index;
        /**
         * current entry
         */
        HashMap.Entry<K, V> current;

        HashIterator() {
            expectedModCount = modCount;
            // advance to first entry
            if (size > 0) {
                HashMap.Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null) {
                    ;
                }
            }
        }

        @Override
        public final boolean hasNext() {
            return next != null;
        }

        final HashMap.Entry<K, V> nextEntry() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            HashMap.Entry<K, V> e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }

            if ((next = e.next) == null) {
                HashMap.Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null) {
                    ;
                }
            }
            current = e;
            return e;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Object k = current.key;
            current = null;
            HashMap.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

    }


    private final class ValueIterator extends HashMap.HashIterator {
        @Override
        public V next() {
            return (V) nextEntry().value;
        }
    }

    private final class KeyIterator extends HashMap.HashIterator {
        @Override
        public K next() {
            return (K) nextEntry().getKey();
        }
    }

    private final class EntryIterator extends HashMap.HashIterator {
        @Override
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    Iterator<K> newKeyIterator() {
        return new HashMap.KeyIterator();
    }

    Iterator<V> newValueIterator() {
        return new HashMap.ValueIterator();
    }

    Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new HashMap.EntryIterator();
    }


    // Views

    private transient Set<Map.Entry<K, V>> entrySet = null;

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     */
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null ? ks : (keySet = new HashMap.KeySet()));
    }

    private final class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return newKeyIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return HashMap.this.removeEntryForKey(o) != null;
        }

        @Override
        public void clear() {
            HashMap.this.clear();
        }
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     */
    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null ? vs : (values = new HashMap.Values()));
    }

    private final class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return newValueIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public void clear() {
            HashMap.this.clear();
        }
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return entrySet0();
    }

    private Set<Map.Entry<K, V>> entrySet0() {
        Set<Map.Entry<K, V>> es = entrySet;
        return es != null ? es : (entrySet = new HashMap.EntrySet());
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return newEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            HashMap.Entry<K, V> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }

        @Override
        public boolean remove(Object o) {
            return removeMapping(o) != null;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            HashMap.this.clear();
        }
    }

    /**
     * Save the state of the <tt>HashMap</tt> instance to a stream (i.e.,
     * serialize it).
     *
     * @serialData The <i>capacity</i> of the HashMap (the length of the
     * bucket array) is emitted (int), followed by the
     * <i>size</i> (an int, the number of key-value
     * mappings), followed by the key (Object) and value (Object)
     * for each key-value mapping.  The key-value mappings are
     * emitted in no particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws IOException {
        Iterator<Map.Entry<K, V>> i =
                (size > 0) ? entrySet0().iterator() : null;

        // Write out the threshold, loadfactor, and any hidden stuff
        s.defaultWriteObject();

        // Write out number of buckets
        s.writeInt(table.length);

        // Write out size (number of Mappings)
        s.writeInt(size);

        // Write out keys and values (alternating)
        if (i != null) {
            while (i.hasNext()) {
                Map.Entry<K, V> e = i.next();
                s.writeObject(e.getKey());
                s.writeObject(e.getValue());
            }
        }
    }

    private static final long serialVersionUID = 362498820763181265L;

    /**
     * Reconstitute the <tt>HashMap</tt> instance from a stream (i.e.,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        // Read in the threshold, loadfactor, and any hidden stuff
        s.defaultReadObject();

        // Read in number of buckets and allocate the bucket array;
        int numBuckets = s.readInt();
        table = new HashMap.Entry[numBuckets];

        init();  // Give subclass a chance to do its thing.

        // Read in size (number of Mappings)
        int size = s.readInt();

        // Read the keys and values, and put the mappings in the HashMap
        for (int i = 0; i < size; i++) {
            K key = (K) s.readObject();
            V value = (V) s.readObject();
            putForCreate(key, value);
        }
    }

    int capacity() {
        return table.length;
    }

    float loadFactor() {
        return loadFactor;
    }
}
