var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var request=require('request');
var http = require('http');
var axios = require('axios');
var app = express();

var appKey = "YijingYa-firstpro-PRD-82eb6be68-9d6aa511"


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());

 
const  cors = require('cors');
app.use(cors());
app.listen(8080);

app.get('/', (req, res, next) =>
{

  res.send("It's backend");
  return res;

});

app.get('/user', (req, res, next) =>
{
  Url =  "https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=YijingYa-firstpro-PRD-82eb6be68-9d6aa511&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&paginationInput.entriesPerPage=100&keywords=mask&itemFilter(0).name=MaxPrice&itemFilter(0).value=50&itemFilter(1).name=MinPrice&itemFilter(1).value=30&itemFilter(0).paramName=Currency&itemFilter(0).paramValue=USD&itemFilter(1).paramName=Currency&itemFilter(1).paramValue=USD&itemFilter(2).name=Condition&itemFilter(2).value=New&itemFilter(3).name=ExpeditedShippingType&itemFilter(3).value=Expedited&sortOrder=PricePlusShippingHighest"

  request(Url, function (error, response, body) {

        if (!error && response.statusCode == 200) {
            bodyJson = JSON.parse(body);
            response = bodyJson;
            res.send(bodyJson);
        }    
})
  return res;
});

app.get('/find', (req, res, next) =>
{

    Url = "https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=";
    Url += appKey;
    Url += "&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&paginationInput.entriesPerPage=50";
    
    addUrl = req.originalUrl.substring(6);

    Url += addUrl;
    obj = [];


    axios.get(Url).then(function (response){
			body = response.data;
			if(body["findItemsAdvancedResponse"][0]["ack"][0] == "Failure"){
				let opp = {
					"ID": "true",}
				obj.push(opp);
				res.send(obj);
			}else if(body["findItemsAdvancedResponse"][0]["searchResult"][0]["@count"] == "0"){
				let opp = {
					"ID": "true",}
				obj.push(opp);
				res.send(obj);
			}else{
				let re_items = selectSimple(body);
				res.send(re_items);
			}
		});

    

  	return res;
});

app.get('/item', (req, res, next) =>
{
	// 254603172743
	id = req.originalUrl.substring(6);

	u = "http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=YijingYa-firstpro-PRD-82eb6be68-9d6aa511&siteid=0&version=967&ItemID=";

	u += id;

	u += "&IncludeSelector=Description,Details,ItemSpecifics";	

	axios.get(u).then(function (response){
		body = response.data;
		res.send(body);
	});

	return res;

});

app.all("*",function(req,res,next){

	if(!req.headers.origin){
		return
	}
     res.header("Access-Control-Allow-Origin","*");
     res.header("Access-Control-Allow-Headers","content-type");
     res.header("Access-Control-Allow-Methods","DELETE,PUT,POST,GET,OPTIONS");
     if (req.method.toLowerCase() == 'options'){
      res.sendStatus(200); 
     }
     else{
      next();
     }
 })
  app.post('/cors',(req,res)=> {
  res.send('ok');
})

app.post('/', async (req, res, next) =>{
	
	var forms = req.body;	
		let obj = {};
		let ebayUrl = buildUrl(forms,res);
		console.log(ebayUrl);
		axios.get(ebayUrl).then(function (response){
			body = response.data;
			if(body["findItemsAdvancedResponse"][0]["ack"][0] == "Failure"){
				obj.result = "Failure"
				res.send(obj);
			}else if(body["findItemsAdvancedResponse"][0]["searchResult"][0]["@count"] == "0"){
				obj.result = "No Result Found"
				res.send(obj);
			}else{
				let re_items = select(body);
				res.send(re_items);
			}
		})
		return res;
});


function selectSimple(bodyJson){
	json = [];
	ack = bodyJson["findItemsAdvancedResponse"][0]["ack"];
	count = bodyJson["findItemsAdvancedResponse"][0]["searchResult"]["@count"];
	y = 0;		
	if(ack=="Success" && count != "0"){
		let items = bodyJson["findItemsAdvancedResponse"][0]["searchResult"][0]["item"];
		for(var i in items){
			if("title" in items[i] && 
				"galleryURL" in items[i] && 
				"__value__" in items[i]["sellingStatus"][0]['convertedCurrentPrice'][0] && 
				"shippingServiceCost" in items[i]['shippingInfo'][0] )
			{
				y++;
				let Title = items[i]['title'][0];
				let Image = items[i]['galleryURL'][0];
				if(Image=="https://thumbs1.ebaystatic.com/pict/04040_0.jpg"){
					Image="default";
				}
				let Price = items[i]['sellingStatus'][0]['convertedCurrentPrice'][0]['__value__'];
				
				let Condition = "N/A";

				if ("condition" in items[i]){
					Condition = items[i]['condition'][0]['conditionDisplayName'][0];
				}

				let ShippingCost = "0.0"
				if("__value__" in items[i]['shippingInfo'][0]['shippingServiceCost'][0]){
					ShippingCost = items[i]['shippingInfo'][0]['shippingServiceCost'][0]['__value__'];
				}

				let Top = items[i]['topRatedListing'][0];
				let id = items[i]['itemId'][0];

				let ship = items[i]['shippingInfo'][0];
				let Link = items[i]['viewItemURL'][0];

				   
				let jsonData = {
					"ID": id,
					"Title": Title,
					"Image" : Image,
					"Price" : Price,
					"Condition" : Condition,
					"ShippingCost" : ShippingCost,
					"Top" : Top,
					"Link" : Link,
					"Ship" : ship				
				}
				json.push(jsonData);
			}
		}
		if(y==0){
			obj = {};
			obj.result="No Result Found";
			return obj;
		}else{
			return json;
		}
	}
}

function select(bodyJson){
	y = 0;
	json = [];
	ack = bodyJson["findItemsAdvancedResponse"][0]["ack"];
	count = bodyJson["findItemsAdvancedResponse"][0]["searchResult"]["@count"];
			
	if(ack=="Success" && count != "0"){
		let items = bodyJson["findItemsAdvancedResponse"][0]["searchResult"][0]["item"];
		for(var i in items){
			if("title" in items[i] && "galleryURL" in items[i] && "viewItemURL" in items[i]  && "__value__" in items[i]["sellingStatus"][0]['currentPrice'][0] && "location" in items[i] && "categoryName" in items[i]['primaryCategory'][0] && "condition" in items[i] && "conditionDisplayName" in items[i]['condition'][0] && "shippingType" in items[i]['shippingInfo'][0] && 'shippingServiceCost' in items[i]['shippingInfo'][0] && '__value__' in items[i]['shippingInfo'][0]['shippingServiceCost'][0] && "shipToLocations" in items[i]['shippingInfo'][0] && "expeditedShipping" in items[i]['shippingInfo'][0] && "oneDayShippingAvailable" in items[i]['shippingInfo'][0] && "bestOfferEnabled" in items[i]['listingInfo'][0] && "buyItNowAvailable" in items[i]['listingInfo'][0] && "listingType" in items[i]['listingInfo'][0] && "gift" in items[i]['listingInfo'][0] && "watchCount" in items[i]['listingInfo'][0])
			{
				console.log(++y);
				let Title = items[i]['title'][0];
				let Image = items[i]['galleryURL'][0];
				if(Image=="https://thumbs1.ebaystatic.com/pict/04040_0.jpg"){
					Image="https://csci571.com/hw/hw8/images/ebayDefault.png";
				}
				let Link = items[i]['viewItemURL'][0];
				let Price = items[i]['sellingStatus'][0]['currentPrice'][0]['__value__'];
				let Location = items[i]['location'][0];
				let Category = items[i]['primaryCategory'][0]['categoryName'][0];
				let Condition = items[i]['condition'][0]['conditionDisplayName'][0];
				let ShippingType = items[i]['shippingInfo'][0]['shippingType'][0];
				let ShippingCost = items[i]['shippingInfo'][0]['shippingServiceCost'][0]['__value__'];
				let ShiptoLocations = items[i]['shippingInfo'][0]['shipToLocations'][0];
				let ExpeditedShipping = items[i]['shippingInfo'][0]['expeditedShipping'][0];
				let OneDayShippingAvailable = items[i]['shippingInfo'][0]['oneDayShippingAvailable'][0];
				let BestOfferEnabled = items[i]['listingInfo'][0]['bestOfferEnabled'][0];
				let BuyItNowAvailable = items[i]['listingInfo'][0]['buyItNowAvailable'][0];
				let ListingType = items[i]['listingInfo'][0]['listingType'][0];
				let Gift = items[i]['listingInfo'][0]['gift'][0];
				let WatchCount = items[i]['listingInfo'][0]['watchCount'][0];
				let jsonData = {
					"BtnID": "id"+y,
					"Title": Title,
					"Image" : Image,
					"Link" : Link,
					"Price" : Price,
					"Location" : Location,
					"Category" : Category,
					"Condition" : Condition,
					"ShippingType" : ShippingType,
					"ShippingCost" : ShippingCost,
					"ShiptoLocations" : ShiptoLocations,
					"ExpeditedShipping" : ExpeditedShipping,
					"OneDayShippingAvailable" : OneDayShippingAvailable,
					"BestOfferEnabled" : BestOfferEnabled,
					"BuyItNowAvailable" : BuyItNowAvailable,
					"ListingType" : ListingType,
					"Gift" : Gift,
					"WatchCount" : WatchCount
				}
				console.log(jsonData);
				json.push(jsonData);
			}
		}
		if(y==0){
			obj = {};
			obj.result="No Result Found";
			return obj;
		}else{
			return json;
		}
	}
}

function buildUrl(forms, res) {
	ebayUrl = "";
	keywords = forms['input_Keyword'];
	minprice = forms['input_MinPrice'];
	maxprice = forms['input_MaxPrice'];
	c1 = forms['condiCheck1'];
	c2 = forms['condiCheck2'];
	c3 = forms['condiCheck3'];
	c4 = forms['condiCheck4'];
	c5 = forms['condiCheck5'];
	sc = forms['sellerCheck'];
	s1 = forms['shipCheck1'];
	s2 = forms['shipCheck2'];

	if (forms['sort'] === "Best Match"){sortOrder = "BestMatch";}
	else if (forms['sort'] ==="Price: highest first"){sortOrder = "CurrentPriceHighest";}
	else if (forms['sort'] ==="Price + Shipping: highest first"){sortOrder = "PricePlusShippingHighest";}
	else{sortOrder = "PricePlusShippingLowest";}
	ebayUrl += "https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=" + appKey + "&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&keywords=";
	ebayUrl += keywords + "&paginationInput.entriesPerPage=100" + "&sortOrder=" + sortOrder;
	i = -1;
	if(minprice !=''){
		i += 1;
		ebayUrl += "&itemFilter(" + i + ").name=MinPrice&itemFilter(" + i
		     + ").value=" + minprice + "&itemFilter(" + i + ").paramName=Currency&itemFilter(" + i + ").paramValue=USD";
	}
	if(maxprice !=''){
		i += 1;
		ebayUrl += "&itemFilter(" + i + ").name=MaxPrice&itemFilter(" +
		    i + ").value=" + maxprice + "&itemFilter(" + i + ").paramName=Currency&itemFilter(" + i + ").paramValue=USD";
	}
	if(c1 != '' || c2 != '' || c3 != '' || c4 != '' || c5 != ''){
		i += 1;
		ebayUrl += "&itemFilter(" + i + ").name=Condition";
		let k = -1;
		if(c1 != ''){
			k += 1;
			ebayUrl += "itemFilter(" + i + ").value(" + k + ")=1000";
		}
	
		if(c2 != ''){
			k += 1;
			ebayUrl += "&itemFilter(" + i + ").value(" + k + ")=3000";
		}
	
		if(c3 != ''){
			k += 1;
			ebayUrl += "&itemFilter(" + i + ").value(" + k + ")=4000";
		}
	
		if(c4 != ''){
			k += 1;
			ebayUrl += "&itemFilter(" + i + ").value(" + k + ")=5000";
		}
	
		if(c5 != ''){
			k += 1;
			ebayUrl += "&itemFilter(" + i + ").value(" + k + ")=6000";
		}
	}
	
	if(sc != ''){
		i += 1;
	    ebayUrl += "&itemFilter(" + i + ").name=ReturnsAcceptedOnly&itemFilter(" + i + ").value=true";
	}

	if(s1 != ''){
		i += 1;
	    ebayUrl += "&itemFilter(" + i + ").name=FreeShippingOnly&itemFilter(" + i + ").value=true";
	}

	if(s2 != ''){
		i += 1;
	    ebayUrl += "&itemFilter(" + i + ").name=ExpeditedShippingType&itemFilter(" + i + ").value=Expedited";
	}

	return ebayUrl;
}


module.exports = app;

