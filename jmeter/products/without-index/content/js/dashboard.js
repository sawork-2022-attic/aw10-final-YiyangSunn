/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9628888888888889, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Books Second Page"], "isController": false}, {"data": [0.905, 500, 1500, "All Products First Page"], "isController": false}, {"data": [0.9615, 500, 1500, "Video Games First Page"], "isController": false}, {"data": [0.987, 500, 1500, "Keyword Search 3 Second Page"], "isController": false}, {"data": [0.953, 500, 1500, "Sports and Outdoors Second Page"], "isController": false}, {"data": [0.9655, 500, 1500, "Sports and Outdoors First Page"], "isController": false}, {"data": [0.934, 500, 1500, "Product Detail 1"], "isController": false}, {"data": [0.9435, 500, 1500, "Keyword Search 1 Second Page"], "isController": false}, {"data": [0.9775, 500, 1500, "Keyword Search 2 Second Page"], "isController": false}, {"data": [0.9615, 500, 1500, "Keyword Search 2 First Page"], "isController": false}, {"data": [0.9255, 500, 1500, "Product Detail 2"], "isController": false}, {"data": [0.9695, 500, 1500, "All Products Second Page"], "isController": false}, {"data": [0.992, 500, 1500, "Books First Page"], "isController": false}, {"data": [0.956, 500, 1500, "Automotive Second Page"], "isController": false}, {"data": [0.982, 500, 1500, "Automotive First Page"], "isController": false}, {"data": [0.977, 500, 1500, "Keyword Search 3 First Page"], "isController": false}, {"data": [1.0, 500, 1500, "Video Games Second Page"], "isController": false}, {"data": [0.9415, 500, 1500, "Keyword Search 1 First Page"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 18000, 0, 0.0, 225.69038888888943, 1, 17176, 11.0, 17.0, 28.0, 7420.980000000003, 428.19420986273997, 641.0422988602303, 69.13552346742156], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Books Second Page", 1000, 0, 0.0, 10.152000000000005, 1, 29, 11.0, 14.0, 17.0, 25.0, 280.3476310625175, 531.1273479114101, 46.54208718811326], "isController": false}, {"data": ["All Products First Page", 1000, 0, 0.0, 281.97600000000034, 1, 4286, 11.0, 1183.299999999971, 2659.2999999999993, 3810.84, 23.833925209142695, 49.08792409490669, 3.6076742259932786], "isController": false}, {"data": ["Video Games First Page", 1000, 0, 0.0, 99.20899999999988, 4, 3017, 12.0, 20.0, 37.0, 2736.99, 25.644971021182744, 39.76976526036057, 4.2574658921885415], "isController": false}, {"data": ["Keyword Search 3 Second Page", 1000, 0, 0.0, 44.162999999999926, 4, 3210, 11.0, 15.0, 18.0, 2372.98, 158.6797841954935, 260.95339897056493, 26.03340209457315], "isController": false}, {"data": ["Sports and Outdoors Second Page", 1000, 0, 0.0, 124.22899999999996, 4, 3022, 11.0, 19.0, 37.94999999999993, 2993.99, 25.642340632852967, 43.72216904552798, 4.282070554900251], "isController": false}, {"data": ["Sports and Outdoors First Page", 1000, 0, 0.0, 78.77099999999982, 4, 3045, 11.0, 20.0, 29.0, 1864.0, 24.614778713139366, 44.08523694032147, 4.110475742135579], "isController": false}, {"data": ["Product Detail 1", 1000, 0, 0.0, 667.7840000000004, 3, 17165, 10.0, 18.0, 6781.95, 14284.0, 27.836543814719963, 7.50281845006124, 3.86014572430687], "isController": false}, {"data": ["Keyword Search 1 Second Page", 1000, 0, 0.0, 383.13199999999915, 3, 10269, 10.0, 16.0, 5373.949999999994, 8163.220000000005, 39.48355509930114, 3.7015832905594817, 6.516328917755755], "isController": false}, {"data": ["Keyword Search 2 Second Page", 1000, 0, 0.0, 81.47999999999983, 1, 5854, 10.0, 15.899999999999977, 20.0, 3213.98, 84.31703204047218, 152.82511461846545, 13.833263069139967], "isController": false}, {"data": ["Keyword Search 2 First Page", 1000, 0, 0.0, 266.1680000000006, 2, 9457, 10.0, 16.0, 30.949999999999932, 8167.180000000004, 53.74899220639613, 90.80314851518409, 8.818194033861865], "isController": false}, {"data": ["Product Detail 2", 1000, 0, 0.0, 653.4619999999994, 3, 17170, 10.0, 20.0, 7177.099999999992, 13372.0, 27.852825669164137, 7.09920654262875, 3.8624035595911206], "isController": false}, {"data": ["All Products Second Page", 1000, 0, 0.0, 72.49000000000011, 4, 1865, 11.0, 18.899999999999977, 25.949999999999932, 1863.0, 24.613566998129368, 45.98206113240868, 3.72568641084966], "isController": false}, {"data": ["Books First Page", 1000, 0, 0.0, 32.128000000000036, 2, 3701, 11.0, 15.0, 18.0, 33.98000000000002, 163.025758069775, 281.60789248451255, 27.064823117052494], "isController": false}, {"data": ["Automotive Second Page", 1000, 0, 0.0, 537.0000000000014, 2, 17176, 11.0, 18.0, 27.0, 14282.98, 27.81099646800345, 54.4269348631699, 4.752855841699808], "isController": false}, {"data": ["Automotive First Page", 1000, 0, 0.0, 128.50600000000028, 3, 12783, 11.0, 18.0, 24.0, 2745.0, 26.61556478228468, 49.25488374820345, 4.548558434472479], "isController": false}, {"data": ["Keyword Search 3 First Page", 1000, 0, 0.0, 104.89299999999994, 4, 8557, 11.0, 16.0, 19.0, 3214.550000000004, 89.3814801573114, 169.63592409277797, 14.664149088308902], "isController": false}, {"data": ["Video Games Second Page", 1000, 0, 0.0, 11.77400000000001, 1, 60, 11.0, 18.0, 25.0, 48.0, 26.597866851078543, 42.93589228362635, 4.415661488948587], "isController": false}, {"data": ["Keyword Search 1 First Page", 1000, 0, 0.0, 485.1100000000003, 2, 17167, 10.0, 17.0, 5093.0, 12199.380000000014, 27.866020174998607, 35.295144694309755, 4.598981845287856], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 18000, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
