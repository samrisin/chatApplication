Welcome to simple chatroom Application.
# Introduction
1. upto 10 clients can be
connected and in a chat room at one time, accepting messages from one client and sending
the messages to all attached clients.
2. Clients will be able to either send a message that is public
in the chat room, or that goes directly to a single, specified client.
3. To add some interest to the chatroom, participants can also send a randomly generated insult.

# How TO RUN
1. open this project in intellij or your preferred iDE.
2. Go to server package and open ChatServer file this file is responsible for running the Server.
3. Now that server is up and running it will tell you the port in command line,copy the port and go to ChatClient and right click you will see the option of modify run configuration then pass the argument "localhost" and the copied "port"; 
4. Run the application, it will ask for your name and voila! you are connected to the chat.

# Commands
1. Type ? to list all the commands
2. logoff: sends a DISCONNECT_MESSAGE to the server
3. who: sends a QUERY_CONNECTED_USERS to the server
4. @user: sends a DIRECT_MESSAGE to the specified user to the server
5. @all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected
6. !user: sends a SEND_INSULT message to the server, to be sent to the specified user

# Example 
Sample command Result
@bob hello bob, how are you? 

@all Hello Everyone!

logoff 

!bob

who 

