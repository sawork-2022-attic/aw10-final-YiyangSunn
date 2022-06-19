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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9924722222222222, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Books Second Page"], "isController": false}, {"data": [0.9425, 500, 1500, "All Products First Page"], "isController": false}, {"data": [0.997, 500, 1500, "Video Games First Page"], "isController": false}, {"data": [1.0, 500, 1500, "Keyword Search 3 Second Page"], "isController": false}, {"data": [1.0, 500, 1500, "Sports and Outdoors Second Page"], "isController": false}, {"data": [0.983, 500, 1500, "Sports and Outdoors First Page"], "isController": false}, {"data": [1.0, 500, 1500, "Product Detail 1"], "isController": false}, {"data": [1.0, 500, 1500, "Keyword Search 1 Second Page"], "isController": false}, {"data": [1.0, 500, 1500, "Keyword Search 2 Second Page"], "isController": false}, {"data": [1.0, 500, 1500, "Keyword Search 2 First Page"], "isController": false}, {"data": [1.0, 500, 1500, "Product Detail 2"], "isController": false}, {"data": [0.98, 500, 1500, "All Products Second Page"], "isController": false}, {"data": [1.0, 500, 1500, "Books First Page"], "isController": false}, {"data": [1.0, 500, 1500, "Automotive Second Page"], "isController": false}, {"data": [0.98, 500, 1500, "Automotive First Page"], "isController": false}, {"data": [1.0, 500, 1500, "Keyword Search 3 First Page"], "isController": false}, {"data": [0.982, 500, 1500, "Video Games Second Page"], "isController": false}, {"data": [1.0, 500, 1500, "Keyword Search 1 First Page"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 18000, 0, 0.0, 25.5543888888888, 1, 2206, 11.0, 19.0, 32.0, 517.9900000000016, 3463.5366557629404, 5889.022700897152, 559.2168558783914], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Books Second Page", 1000, 0, 0.0, 11.823000000000008, 1, 37, 11.0, 16.0, 20.0, 27.0, 480.0768122899664, 909.5223985837733, 79.70025204032645], "isController": false}, {"data": ["All Products First Page", 1000, 0, 0.0, 139.40800000000004, 6, 2206, 11.0, 662.2999999999839, 1271.9499999999998, 1785.1800000000007, 196.23233908948197, 574.9937144328885, 29.703137264521192], "isController": false}, {"data": ["Video Games First Page", 1000, 0, 0.0, 19.825000000000017, 3, 511, 11.0, 31.0, 49.0, 112.93000000000006, 332.22591362126246, 515.535779173588, 55.1546926910299], "isController": false}, {"data": ["Keyword Search 3 Second Page", 1000, 0, 0.0, 12.261999999999988, 4, 40, 11.0, 17.0, 27.0, 30.99000000000001, 469.26325668700144, 1125.037577721727, 76.98850305021118], "isController": false}, {"data": ["Sports and Outdoors Second Page", 1000, 0, 0.0, 17.57300000000001, 3, 110, 11.0, 31.0, 67.89999999999986, 99.0, 324.67532467532465, 603.0587332589286, 54.21824269480519], "isController": false}, {"data": ["Sports and Outdoors First Page", 1000, 0, 0.0, 35.95899999999996, 1, 871, 12.0, 33.89999999999998, 82.0, 566.98, 276.93159789531984, 478.6946569509831, 46.24541332040985], "isController": false}, {"data": ["Product Detail 1", 1000, 0, 0.0, 11.586000000000002, 4, 52, 10.0, 17.0, 24.0, 30.0, 430.10752688172045, 115.9274193548387, 59.64381720430107], "isController": false}, {"data": ["Keyword Search 1 Second Page", 1000, 0, 0.0, 12.296000000000005, 4, 81, 10.0, 17.899999999999977, 24.0, 62.840000000000146, 443.45898004434594, 41.57427937915743, 73.18805432372505], "isController": false}, {"data": ["Keyword Search 2 Second Page", 1000, 0, 0.0, 12.537000000000008, 4, 43, 11.0, 18.0, 26.949999999999932, 39.99000000000001, 457.6659038901602, 745.4921696224256, 75.0858123569794], "isController": false}, {"data": ["Keyword Search 2 First Page", 1000, 0, 0.0, 12.176999999999984, 4, 67, 10.0, 17.899999999999977, 24.0, 38.99000000000001, 451.46726862302484, 840.324315744921, 74.06884875846501], "isController": false}, {"data": ["Product Detail 2", 1000, 0, 0.0, 11.850000000000003, 4, 65, 10.0, 16.899999999999977, 27.0, 48.0, 434.4048653344917, 110.2985345623371, 60.23973718505647], "isController": false}, {"data": ["All Products Second Page", 1000, 0, 0.0, 38.047000000000004, 2, 873, 11.0, 33.099999999999795, 77.0, 556.98, 274.649821477616, 771.9151246223565, 41.57297102444383], "isController": false}, {"data": ["Books First Page", 1000, 0, 0.0, 12.410999999999996, 1, 38, 11.0, 17.0, 25.0, 31.0, 474.3833017077799, 819.9757063863851, 78.75504032258064], "isController": false}, {"data": ["Automotive Second Page", 1000, 0, 0.0, 12.790000000000004, 3, 78, 11.0, 18.0, 27.0, 40.0, 424.08821034775235, 828.7366975455894, 72.4760125106022], "isController": false}, {"data": ["Automotive First Page", 1000, 0, 0.0, 38.246000000000016, 4, 748, 11.0, 20.0, 42.94999999999993, 535.96, 348.91835310537334, 645.4369384595254, 59.62960136078158], "isController": false}, {"data": ["Keyword Search 3 First Page", 1000, 0, 0.0, 12.381999999999994, 3, 43, 11.0, 18.0, 27.949999999999932, 35.0, 461.2546125461255, 1055.3852376902676, 75.6745848708487], "isController": false}, {"data": ["Video Games Second Page", 1000, 0, 0.0, 36.30300000000004, 4, 732, 11.0, 26.0, 52.94999999999993, 516.99, 343.4065934065934, 555.0158154833448, 57.01086023351648], "isController": false}, {"data": ["Keyword Search 1 First Page", 1000, 0, 0.0, 12.503999999999985, 5, 82, 10.0, 18.0, 31.0, 65.99000000000001, 438.4042086804033, 828.8553882617273, 72.35381959666812], "isController": false}]}, function(index, item){
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
