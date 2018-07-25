chart-designer
==============

Ineteractive tool to select charts for offline chart bundles

The chart designer is a tool to interactivly produce a catalog. contains a list of charts which can be combined into a single chart bundle for usage with offline apps or printing.

Implemented formats (as of 2015-08):
* OsmAnd (raster charts)
* OruxMaps (raster charts)

Planned formats:
* OpenCPN KAP files

chart-designer
==============
Der Chart-Designer dient dazu, Kartenausschnitte in passenden Zoomleveln zu einem "Atlas" zusammenzustellen. 


compiling/starting
==============
You need chart-base compiled next to the chart-designer in a folder named OSeaMChartBase: 
```
git clone https://github.com/OpenSeaMap/chart-designer.git
git clone https://github.com/OpenSeaMap/chart-base.git OSeaMChartBase
cd OSeaMChartBase
ant
cd ..
cd chart-designer
ant
```

sqlite is required => download the char to the char-designer folder
run it:
```
 java -classpath OSeaMChartDesigner.jar:sqlite-jdbc-3.16.1.jar osmcd.StartOSMCD
```
