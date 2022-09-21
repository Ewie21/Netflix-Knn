use std::io::{BufRead, BufReader};
use std::collections::HashMap;



fn main(){
    get_ratings();
}
fn get_ratings(){
    //init hashmap
    let mut MU_map = HashMap::new(); //put in array here
    //grabs file
    let ratings = std::fs::File::open("ratings.txt").unwrap();
    let movies = std::fs::File::open("ratings.txt").unwrap();
    //creates file reader to interate through the ratings file
    let reader = BufReader::new(ratings);
    let mut ratings_vec = vec![];
    let mut new_ratings_vec  = vec![];
    //iterate through the ratings file
        for (index, line) in reader.lines().enumerate(){
            let line:String = line.unwrap();
            //putting the nums list from the rating into the arr 
            ratings_vec[index] = line;
            //splits the current ratings_vec index into a new sub-vector
            let ratings_sub_vec:Vec<&str> = ratings_vec[index].split(";").collect();
            //check if movie_id is the same as before
            //if not make a new hashmap entry
            let movie_id = ratings_sub_vec[0];
            let old_id = &ratings_vec[index];
            
            if movie_id!=old_id{
                //convert the (old)movie_id into an int
                let old_id:u32 = old_id.parse().unwrap();
                //converts the ratings_vec into a ratings_vec filled with ints
                for c in 0..ratings_sub_vec.len()-1{
                    
                    //new sub-vec for the ints
                    let mut ratings_sub_vec1 = vec![];
                    
                    //converts the sub_vector to ints
                    for n in 0..2{
                        let a = ratings_sub_vec[n];
                        ratings_sub_vec1[n] = a.parse::<u32>().unwrap();
                        //ratings_sub_vec1[n] = ratings_sub_vec[n].parse::<u32>();
                    }
                    //applies the new sub-vectors to a new_ratings_vec
                    &new_ratings_vec[c] = ratings_sub_vec1; 
                    
                }
                //inserts into the vec
                MU_map.insert(old_id,&new_ratings_vec);
                //resets the vec
                ratings_vec.drain(..);
                &new_ratings_vec.drain(..);
            }

            
        }
}

//remember to convert the ratings data back to ints to do calc