struct Rectangle{
    width: i8,
    height: i8,
}

impl Rectangle{
    fn area(&self)->i8{
        self.width*self.height
    }

    fn width(&self)->bool{
        self.width > 0
    }
}

fn main(){
    let rect1 = Rectangle{
        width: 10,
        height: 9,
    };
    println!("The area of the rect is {} pixels", rect1.area());
    count(10);
}

fn count(x:i8){
    let mut count1:i8 = 0;    
    for _x in 0..x{
        count1 += 1;
        println!("{}", count1.to_string());
    }
}
