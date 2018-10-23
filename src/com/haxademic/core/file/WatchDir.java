package com.haxademic.core.file;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * Example to watch a directory (or tree) for changes to files.
 * // https://docs.oracle.com/javase/8/docs/api/java/nio/file/StandardWatchEventKinds.html#ENTRY_CREATE
 * // https://docs.oracle.com/javase/tutorial/essential/io/notification.html#try
 */

public class WatchDir {

	public interface IWatchDirListener {
		
		public static int ENTRY_CREATE = 0;
		public static int ENTRY_MODIFY = 1;
		public static int ENTRY_DELETE = 2;
		
		public void dirUpdated(int eventType, String filePath);
	}

	private WatchService watcher;
	private Map<WatchKey,Path> keys;
	private boolean recursive;
	private boolean trace = false;
	private IWatchDirListener delegate;
	public static boolean DEBUG = false;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>)event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				if(DEBUG) System.out.format("register: %s\n", dir);
			} else {
				if (!dir.equals(prev)) {
					if(DEBUG) System.out.format("update: %s -> %s\n", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public WatchDir(String directory, boolean recursive, IWatchDirListener delegate) {
		this.recursive = recursive;
		this.delegate = delegate;

		// https://docs.oracle.com/javase/8/docs/api/java/nio/file/StandardWatchEventKinds.html#ENTRY_CREATE
		// https://docs.oracle.com/javase/tutorial/essential/io/notification.html#try
		new Thread(new Runnable() { public void run() {
			try {
				// register directory and process its events
				Path dir = Paths.get(directory);
				init(dir, recursive);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}}).start();
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	protected void init(Path dir, boolean recursive) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey,Path>();

		if (recursive) {
			if(DEBUG) System.out.format("Scanning %s ...\n", dir);
			registerAll(dir);
			if(DEBUG) System.out.println("Done.");
		} else {
			register(dir);
		}

		// enable trace after initial registration
		this.trace = true;
		processEvents();
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	void processEvents() {
		for (;;) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event: key.pollEvents()) {
				@SuppressWarnings("rawtypes")
				WatchEvent.Kind kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				if(DEBUG) System.out.format("%s: %s\n", event.kind().name(), child);
				
				// delegate callback
				int eventType = -1;
				if(kind == StandardWatchEventKinds.ENTRY_CREATE) eventType = IWatchDirListener.ENTRY_CREATE;
				else if(kind == StandardWatchEventKinds.ENTRY_MODIFY) eventType = IWatchDirListener.ENTRY_MODIFY;
				else if(kind == StandardWatchEventKinds.ENTRY_DELETE) eventType = IWatchDirListener.ENTRY_DELETE;
				delegate.dirUpdated(eventType, child.toString());
				

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == StandardWatchEventKinds.ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException x) {
						// ignore to keep sample readbale
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}
}