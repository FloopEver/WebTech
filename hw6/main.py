from flask import Flask
from flask import render_template, request
import datetime
from ebaysdk.finding import Connection
import json
import urllib.request
from flask import jsonify

app = Flask(__name__)

@app.route("/json")
def get_data():
    url =  "https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=YijingYa-firstpro-PRD-82eb6be68-9d6aa511&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&keywords=mask&paginationInput.entriesPerPage=10&itemFilter(0).name=MaxPrice&itemFilter(0).value=50&itemFilter(1).name=MinPrice&itemFilter(1).value=30&itemFilter(0).paramName=Currency&itemFilter(0).paramValue=USD&itemFilter(1).paramName=Currency&itemFilter(1).paramValue=USD&itemFilter(2).name=Condition&itemFilter(2).value=New&itemFilter(3).name=ExpeditedShippingType&itemFilter(3).value=Expedited&sortOrder=PricePlusShippingHighest"


    urlinfo = urllib.request.urlopen(url)
    data = json.loads(urlinfo.read())
    jdata = jsonify(data)

    return jdata


@app.route("/", methods=["GET", "POST"])
def index():
    if request.method == "GET":

        info = dict()
        info["key_words"] = request.args.get("key_words")   # String
        info["min_price"] = request.args.get("MinPrice")    # String Integer
        info["max_price"] = request.args.get("MaxPrice")    # String Integer

        info["c1"] = request.args.get("condition1") # String
        info["c2"] = request.args.get("condition2") # String
        info["c3"] = request.args.get("condition3") # String
        info["c4"] = request.args.get("condition4") # String
        info["c5"] = request.args.get("condition5") # String

        info["seller"] = request.args.get("seller") # String
        info["s1"] = request.args.get("shipping1") # String
        info["s2"] = request.args.get("shipping2") # String

        info["sorts"] = request.args.get("sorts") # String
        info["help"] = "flag"
        try:
            if not info["key_words"]:
                info["help"] = ""
                return render_template("index.html", info=info)
        except:
            pass

        try:
            if float(info["min_price"]) < 0:
                info["help"] = ""
                return render_template("index.html", info=info)
        except:
            pass

        try:
            if float(info["max_price"]) < 0:
                info["help"] = ""
                return render_template("index.html", info=info)
        except:
            pass

        try:
            if float(info["max_price"]) < float(info["min_price"]):
                info["help"] = ""
                return render_template("index.html", info=info)
        except:
            pass
            

        # TODO: Query Data from Ebay API
        # TODO: Format the data and pass to HTML

        data = dict()
        # try:
        #     print("Trying...")
        #     api_key = "YijingYa-firstpro-PRD-82eb6be68-9d6aa511"
        #     # Connect
        #     api = Connection(appid = api_key, config_file = None)
        #     data = search(api, info)
        #     ls = data["items"]

        # except:
        #     data["total"] = "0"
        #     ls = []
        #     print("Fail")

        print("Trying...")
        api_key = "YijingYa-firstpro-PRD-82eb6be68-9d6aa511"
        # Connect
        api = Connection(appid = api_key, config_file = None)
        data = search(api, info)
        ls = data["items"]


    print(data)
    print("------------")
    return render_template("index.html", info=info, data=data, ls=ls)

def search(api, info):
    print("Start searching...")
    keywords = info["key_words"]
    min_price = info["min_price"]
    max_price = info["max_price"]
    sort_order = info["sorts"]

    seller = info["seller"]
    if seller:
        returnsAcceptedOnly = "true"
    else:
        returnsAcceptedOnly = "false"

    shipping1 = info["s1"]
    shipping2 = info["s2"]

    if shipping1:
        freeShippingOnly = "true"
    else:
        freeShippingOnly = "false"

    conditions = []
    if info["c1"]:
        conditions.append("1000")

    if info["c2"]:
        conditions.append("3000")

    if info["c3"]:
        conditions.append("4000")

    if info["c4"]:
        conditions.append("5000")

    if info["c5"]:
        conditions.append("6000")


    print("get api connection")


    #####################
    if conditions:
        if shipping2: 
            if min_price and max_price:
                print("Clause 16")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MinPrice", "value": min_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "MaxPrice", "value": max_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "ExpeditedShippingType", "value": "Expedited"},
                        {"name": "Condition", "value": conditions},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )

            elif min_price:
                print("Clause 15")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MinPrice", "value": min_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "ExpeditedShippingType", "value": "Expedited"},
                        {"name": "Condition", "value": conditions},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )
            elif max_price:
                print("Clause 14")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MaxPrice", "value": max_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "ExpeditedShippingType", "value": "Expedited"},
                        {"name": "Condition", "value": conditions},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )
            else:
                print("Clause 13")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "ExpeditedShippingType", "value": "Expedited"},
                        {"name": "Condition", "value": conditions},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )

        else: 
            if min_price and max_price:
                print("Clause 12")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MinPrice", "value": min_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "MaxPrice", "value": max_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "Condition", "value": conditions},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )

            elif min_price:
                print("Clause 11")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MinPrice", "value": min_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "Condition", "value": conditions},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )
            elif max_price:
                print("Clause 10")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MaxPrice", "value": max_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "Condition", "value": conditions},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )
            else:
                print("Clause 9")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "Condition", "value": conditions},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )

    else:
        if shipping2: 
            if min_price and max_price:
                print("Clause 8")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MinPrice", "value": min_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "MaxPrice", "value": max_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "ExpeditedShippingType", "value": "Expedited"},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )

            elif min_price:
                print("Clause 7")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MinPrice", "value": min_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "ExpeditedShippingType", "value": "Expedited"},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )
            elif max_price:
                print("Clause 6")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MaxPrice", "value": max_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "ExpeditedShippingType", "value": "Expedited"},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )
            else:
                print("Clause 5")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                        {"name": "ExpeditedShippingType", "value": "Expedited"},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )

        else: 
            if min_price and max_price:
                print("Clause 4")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MinPrice", "value": min_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "MaxPrice", "value": max_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )

            elif min_price:
                print("Clause 3")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MinPrice", "value": min_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )
            elif max_price:
                print("Clause 2")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "MaxPrice", "value": max_price, "paramName": "Currency", "paramValue": "USD"},
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )
            else:
                print("Clause 1")
                api.execute("findItemsAdvanced", {
                    "keywords": keywords,
                    "itemFilter": [
                        {"name": "ReturnsAcceptedOnly", "value": returnsAcceptedOnly},
                        {"name": "FreeShippingOnly", "value": freeShippingOnly},
                    ],
                    "paginationInput": {
                        "entriesPerPage": "10",
                        "pageNumber": "1",	 
                    },
                    "sortOrder": sort_order,
                    }
                )


    ###########################################
    print("get execute")

    # Get search items
    items = api.response.dict()

    # Get parser
    data = parser(items)

    # print(len(items["searchResult"]["item"]))
    print("finished search?")

    return data


def parser(items):
    print("Get parser")
    ls = []
    data = dict()

    print(items)
    data["total"]  = items["paginationOutput"]["totalEntries"]

    idx = 0
    for item in items["searchResult"]["item"]:
        tmp = dict()
        

        try:
            tmp["url_image"] = item["galleryURL"]
        except:
            tmp["url_image"] = "false"

        try: 
            tmp["title"] = item["title"]
        except:
            continue


        try: 
            tmp["category"] = item["primaryCategory"]["categoryName"]
        except:
            continue


        tmp["redirection"] = item["viewItemURL"]

        try: 
            tmp["condition"] = item["condition"]["conditionDisplayName"]
        except:
            continue


        tmp["top_rate"] = item["topRatedListing"]
        tmp["return"] = item["returnsAccepted"]

        

        

        if item["shippingInfo"]["expeditedShipping"] == "true":
            tmp["exp_ship"] = "1"
        else:
            tmp["exp_ship"] = "0"

        try:
            float(item["sellingStatus"]["convertedCurrentPrice"]["value"])
        except:
            continue

        try:

            if float(item["shippingInfo"]["shippingServiceCost"]["value"]):
                tmp["free_ship"] = "0"
            else:
                tmp["free_ship"] = "1"

            price = float(item["sellingStatus"]["convertedCurrentPrice"]["value"]) + float(item["shippingInfo"]["shippingServiceCost"]["value"])
            tmp["price"] = str(price)
            tmp["ship_price"] = item["shippingInfo"]["shippingServiceCost"]["value"]
            tmp["current_price"] = item["sellingStatus"]["convertedCurrentPrice"]["value"]

        except:
            tmp["free_ship"] = "0"
            tmp["ship_price"] = "0.0"
            tmp["price"] = item["sellingStatus"]["convertedCurrentPrice"]["value"]
            tmp["current_price"] = item["sellingStatus"]["convertedCurrentPrice"]["value"]






        tmp["index"] = "item" + str(idx)
        idx = idx + 1
        tmp["location"] = item["location"]
        ls.append(tmp)

    data["items"] = ls
    return data

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080, debug=True)
