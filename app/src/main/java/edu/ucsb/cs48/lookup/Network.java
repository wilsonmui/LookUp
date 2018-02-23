

/*
    This class will be a singleton used as a universal network. It will show which Users are friends with eachother.
 */

public class Network {

    private static Network network;

    private Network() {
        // Required empty constructor for Firebase
    }

    public static Singleton getInstance(){
        if(network == null){
            network = new Network();
        }

        return network;
    }

    /*
        Values for Network should be loaded from database.
        Changes to Network should be written to database.
         */

}