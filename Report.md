## question 1:
### part 1)
The output of t1.run() will display the name of the main thread, since the run() method is executed within the same thread that runs the main method.

In contrast, the output of t2.start() will show the name of a new thread, which operates independently from the main thread. This happens because calling the start() method instructs the operating system to create and run a separate thread.

### part 2)
when we invoke the start() method on Thread, the OS will execute run() method of the target class in a new, separate thread.

However, if we call the run() method directly, it is executed just like a normal method call, in the same thread that invoked it, typically the main thread.

## question 2:
### part 1)
Well, This is a daemon thread, which means it will be terminated as soon as all non-daemon threads finish their execution.

As a result, we may either not see the output "Daemon thread running..." at all, or we might see it printed once before "Main thread ends." appears, depending on OS timing.

### part 2)
In this case, the program will continue running until the message "Demon thread running..." is printed 20 times.

That's because the thread is no longer a daemon and now the program waits for this non-daemon thread to finish its work before exiting.

### part 3)
Great question - and a great answer!

- Saving progress *MUST NOT BE DONE IN A DAEMON THREAD*, since it's critical and must be completed before the app shuts down.
- However, auto-saving is better to be done as a daemon thread! Because we don't want the app to delay its exiting just to complete the auto-save.
- Garbage Collection is also better to run in a daemon thread that dosen't block the application from terminating when it's done.

## question 3:
### part 1)
just a single line: "Thread is running using a ...!"

### part 2)
Lambda Expression.

### part 3)
there is no need to create a separated class to define the runnable part.

However, it's important to note that Lambda expression can only be used when the target is a single function.

So, if you need to implement multiple methods in the class that runs on a separate thread, a Lambda expression won't be suitable. In such cases, it's better to define a dedicated class that extends Thread or implements Runnable.