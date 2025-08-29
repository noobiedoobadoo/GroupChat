# GroupChat

CLI based chat program where users can write and read messages

to start server
```
javac Server.java
java Server
```
similarly for client
```
javac Client.java
java Client
```
Enter username/alias , if it's already in use you're prompted again
![Screenshot 2024-10-29 210404](https://github.com/user-attachments/assets/2b87b119-a653-4ec6-ad2a-c14bdc15785a)
* registering for a username/alias is atomic

In the below screenshot you can see that users can read & write
connection is maintained until client decides to disconnect using /quit
![Screenshot 2024-10-29 203557](https://github.com/user-attachments/assets/9bebb34b-f452-4125-8f65-5ca8233d348d)

## Implementation
* Server listens for incoming connections
* When a client connects new thread is spawned corresponding to that client
* A list of all connected clients is maintained
* **If multiple clients register for same alias only 1 is allowed and rest are blocked (alias allocation is Atomic)**
* When a client sends a message, it is broadcasted to all connected clients
*  If a client disconnects the connection is removed from the list
  


