import java.util.concurrent.atomic.AtomicInteger;


public class Demo {
	public static void main(String[] args) {
		
		final AtomicInteger atomicInteger = new AtomicInteger(1);
		
		new Thread(new Runnable() {
			int turn = 5;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					synchronized (atomicInteger) {
						while (atomicInteger.get() != this.turn) {
							atomicInteger.wait();
						}
						System.out.println(this.turn);
						atomicInteger.incrementAndGet();
						atomicInteger.notifyAll();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
		new Thread(new Runnable() {
			int turn = 4;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					synchronized (atomicInteger) {
						while (atomicInteger.get() != this.turn) {
							atomicInteger.wait();
						}
						System.out.println(this.turn);
						atomicInteger.incrementAndGet();
						atomicInteger.notifyAll();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
		new Thread(new Runnable() {
			int turn = 3;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					synchronized (atomicInteger) {
						while (atomicInteger.get() != this.turn) {
							atomicInteger.wait();
						}
						System.out.println(this.turn);
						atomicInteger.incrementAndGet();
						atomicInteger.notifyAll();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
		new Thread(new Runnable() {
			int turn = 2;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					synchronized (atomicInteger) {
						while (atomicInteger.get() != this.turn) {
							atomicInteger.wait();
						}
						System.out.println(this.turn);
						atomicInteger.incrementAndGet();
						atomicInteger.notifyAll();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
		new Thread(new Runnable() {
			int turn = 1;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					synchronized (atomicInteger) {
						while (atomicInteger.get() != this.turn) {
							atomicInteger.wait();
						}
						System.out.println(this.turn);
						atomicInteger.incrementAndGet();
						atomicInteger.notifyAll();
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
		
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
