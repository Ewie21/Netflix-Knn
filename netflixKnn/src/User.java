package src;
import java.util.HashMap;
import java.lang.Math;


public class User implements Comparable<User> {
    //key:movie, value:rating
    HashMap<Integer, Integer> movies = new HashMap<Integer, Integer>();  
    int userId;
    double distance;
    public User(int userId){
        this.userId = userId;
    }
    public HashMap<Integer, Integer> getMovies(){
        return movies;
    }
    public int getId(){
        return userId;
    }
    public void addMovie(int movieId, int movieRating){
        movies.put(movieId, movieRating);
    }

    public void computeDistance(User user){
        double sum = 0.00;
        int count = 0;
        int moviesSeen = 0;
        for(int i:movies.keySet()){
            if(user.movies.containsKey(i)){
                sum += Math.pow(movies.get(i) - user.movies.get(i),2); //this is computing the distance from main user to this user and main user to main user which is wrong
                count++;
                moviesSeen++;
            }
        }
        moviesSeen = moviesSeen/4;
        distance = Math.sqrt(sum/count);
    }


    @Override
    public int compareTo(User o) {
       return Double.compare(distance, o.distance); //fix
    }
}
