package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A thread-safe version of Inverted Index using a read/write lock.
 * 
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class ThreadedInvertedIndex extends InvertedIndex {

	/** The lock used to protect concurrent access to the underlying inverted index. */
	private final SimpleReadWriteLock lock;
	
	/**
	 * Initializes ReadWriteLock
	 */
	public ThreadedInvertedIndex() {
		this.lock = new SimpleReadWriteLock();
	}

	@Override
	public void addAll(List<String> listStems, String inputPath) {
		lock.writeLock().lock();
		try {
			super.addAll(listStems, inputPath);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex otherInvertedIndex) {
		lock.writeLock().lock();
		try {
			super.addAll(otherInvertedIndex);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void add(String word, String location, Integer position) {
		lock.writeLock().lock();
		try {
			super.add(word, location, position);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public List<SearchResult> exactSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> partialSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return super.size();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int size(String word) {
		lock.readLock().lock();
		try {
			return super.size(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int size(String word, String location) {
		lock.readLock().lock();
		try {
			return super.size(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word) {
		lock.readLock().lock();
		try {
			return super.contains(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word, String location) {
		lock.readLock().lock();
		try {
			return super.contains(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word, String location, Integer position) {
		lock.readLock().lock();
		try {
			return super.contains(word, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void writeJson(Path writePath) throws IOException {
		lock.readLock().lock();
		try {
			super.writeJson(writePath);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void writeCount(Path writePath) throws IOException {
		lock.readLock().lock();
		try {
			super.writeCount(writePath);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Map<String, Integer> getCount() {
		lock.readLock().lock();
		try {
			return super.getCount();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> get() {
		lock.readLock().lock();
		try {
			return super.get();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> get(String word) {
		lock.readLock().lock();
		try {
			return super.get(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> get(String word, String location) {
		lock.readLock().lock();
		try {
			return super.get(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}

}
