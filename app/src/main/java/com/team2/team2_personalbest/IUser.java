package com.team2.team2_personalbest;

import android.util.Pair;

import java.util.List;

public abstract class IUser {

    //protected List<user> friendlist;


    /*
        pre: ID is a user
        Used to add an app user to another App users FriendList given ID
     */
    abstract boolean addFriend(int ID);

    /*
        Used to check if given friend's ID is already a friend of user
        ie: is in Users/UserID/Friends
     */
    abstract boolean isFriend(int ID);

    /*
        pre: ID is a user
        Pre: Given ID is Users friend
        Used to remove a friend
        NOTE: delayed for this milestone, ignore unless everything else is done
     */
    abstract boolean removeFriend(int ID);

    /*
        pre: ID is a user and is a friend
        Used to get a friends History of walks
     */
    abstract List<Pair<Integer, Integer>> getWalks(int ID);

    /*
        true if user has init walks.
     */
    abstract boolean hasWalks(int ID);

    /*
        Set 30 days Walks for this user in firebase
     */
    abstract void setWalks(List<Day> walks);

    /*
        Used to get users most current friend list
     */
    abstract List<User> getFriendList();

    /*
        Scans list of users and returns true or false
        Should be Used for checkers before add/remove friend
     */
    abstract boolean isUser(int ID);

    /*
        Scans our list of users and returns user given ID
     */
    abstract User getAppUser(int ID);

    /*
       Adds a user to our total list of users
       Meant to be used on sign up
     */
    //abstract void addUser(user user);

    /*
        Private class user
        Note: user is also a 'user' datatype and should be initialized as such
     */
    static class User {
        String name;
        String address;
        //String isPending;
        int userID;

        public User(String name, String address /*, String isPending */){
            this.name = name;
            this.address = address;
            //this.isPending = isPending;
            this.userID = UserUtilities.emailToUniqueId(address);
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

//        public String getIsPending(){
//            return isPending;
//        }

        public int getUserID(){
            return userID;
        }



//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public void setAddress(String address) {
//            this.address = address;
//        }
//
//        public void setPending(String pending) {
//            isPending = pending;
//        }


        // Added by Daniel to be able to print the user info in the friends list
        public String toString() {
            return "  Name: " + this.name + "   |     Email: " + this.address;
        }
    }
}
