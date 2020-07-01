function testValues() {
    var key_words = document.getElementById("key_words").value;
    this.console.log(key_words);
    
    var min_price = document.getElementById("MinPrice").value;
    var max_price = document.getElementById("MaxPrice").value;

    if(min_price < 0.0 || max_price < 0.0){
        alert("Price Range values cannot be negative! Please try a value greater than or equal to 0.0");
        return;
    }

    if(min_price){
        if(max_price){
        
            if (parseFloat(min_price) > parseFloat(max_price)){
                alert("Oops! Lower price limit cannot be greater than upper price limit! Please try again.");
                return;
            }
        }
    }

    showLess()
    

    
}


function clearValues(){

    try{
        document.getElementById("key_words").value = "";
        document.getElementById("MinPrice").value = "";
        document.getElementById("MaxPrice").value = "";

        document.getElementById("condition1").checked = false;
        document.getElementById("condition2").checked = false;
        document.getElementById("condition3").checked = false;
        document.getElementById("condition4").checked = false;
        document.getElementById("condition5").checked = false;

        document.getElementById("seller").checked = false;

        document.getElementById("shipping1").checked = false;
        document.getElementById("shipping2").checked = false;

        document.getElementById("sorts").selectedIndex = "0"

        document.getElementById("div2").remove()
    }
    catch{
        return
    }
}
function showMore(){
    console.log("Show more")
    document.getElementById("show_more").style.display = "none"
    document.getElementById("show_less").style.display = "block"

    for(i=4; i<=10; i++){
        document.getElementById("item" + i).style.display = "block"
    }


}

function showLess(){
    console.log("Show less")
    
    document.getElementById("show_less").style.display = "none"
    document.getElementById("show_more").style.display = "block"

    for(i=4; i<=10; i++){
        document.getElementById("item" + i).style.display = "none"
    }
}


function extendCard1(x, price, ship_price, location){

    var _div = document.getElementById(x)


    _div.className = "card2"
    document.getElementById("title_"+x).removeAttribute("class")
    document.getElementById("dis_" + x).style.display = "block"
    document.getElementById("img1_" + x).className = "img2"

    if(parseFloat(ship_price)) {
        txt = "<strong>Price: $" + price + " (+ $" + ship_price + " for shipping) </strong><span style = \"font-style:italic;\"> From " + location +"</span>"
    }
    else{
        txt = "<strong>Price: $" + price + " </strong><span style = \"font-style:italic;\"> From " + location + "</span>"
    }

    document.getElementById("price_"+x).innerHTML = txt
    document.getElementById("closeX_"+x).style.display = "block"


    location = location.replace(" ", "+")
    
    txt2 =  "<button type=\"button\" class=\"xbtton\" onclick=closeCard(\'" + x + "\','"+ price + "','" + ship_price + "','" + location + "\')>&#x274C</button>"
    document.getElementById("closeX_"+x).innerHTML = txt2
    console.log(txt2)


}

function closex(){
    console.log("close")
}

function closeCard(x, price, ship_price, location){
    console.log(x)
    var _div = document.getElementById(x)

    _div.className = "card1"
    
 
    
    document.getElementById("title_"+x).className = "fold"
    document.getElementById("dis_" + x).style.display = "none"

    if(parseFloat(ship_price)) {
        txt = "<strong>Price: $" + price + " (+ $" + ship_price + " for shipping)</strong>"
    }
    else{
        txt = "<strong>Price: $" + price + " </strong>"
    }
    
    document.getElementById("img1_" + x).className = "img1"


    document.getElementById("price_"+x).innerHTML = txt
    document.getElementById("closeX_"+x).style.display = "none"
    document.getElementById("closeX_"+x).setAttribute("onClick", "closex()")
    document.getElementById("closeX_"+x).innerHTML = ""
    
}
