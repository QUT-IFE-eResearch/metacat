var MapBuilder_Release=true;

var mbTimerStart=new Date();
var config;
if(typeof baseDir=="undefined"){
var baseDir;
}
var mbServerConfig="mapbuilderConfig.xml";
var mbNsUrl="http://mapbuilder.sourceforge.net/mapbuilder";
var MB_UNLOADED=0;
var MB_LOAD_CORE=1;
var MB_LOAD_CONFIG=2;
var MB_LOADED=3;
function Mapbuilder(){
this.loadState=MB_UNLOADED;
this.loadingScripts=new Array();
this.orderedScripts=new Array();
this.scriptLoader=null;
this.scriptsTimer=null;
this.checkScriptsLoaded=function(){
if(document.readyState!=null){
while(this.loadingScripts.length>0&&(this.loadingScripts[0].readyState=="loaded"||this.loadingScripts[0].readyState=="complete"||this.loadingScripts[0].readyState==null)){
this.loadingScripts.shift();
}
if(this.loadingScripts.length==0&&this.orderedScripts.length==0){
this.setLoadState(this.loadState+1);
}
}else{
if(this.loadState==MB_LOAD_CORE&&config!=null){
this.setLoadState(MB_LOAD_CONFIG);
}
}
};
this.setLoadState=function(_1){
this.loadState=_1;
switch(_1){
case MB_LOAD_CORE:
this.loadOrdered=true;
this.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
this.loadScript(baseDir+"/util/sarissa/Sarissa.js");
this.loadScript(baseDir+"/util/sarissa/javeline_xpath.js");
this.loadScript(baseDir+"/util/sarissa/javeline_xslt.js");
this.loadScript(baseDir+"/util/sarissa/sarissa_dhtml.js");
this.loadScript(baseDir+"/util/sarissa/sarissa_ieemu_xpath.js");
this.loadScript(baseDir+"/util/proj4js/proj4js-compressed.js");
this.loadScript(baseDir+"/util/Util.js");
this.loadScript(baseDir+"/util/Listener.js");
this.loadScript(baseDir+"/model/ModelBase.js");
this.loadScript(baseDir+"/model/Config.js");
this.loadOrdered=false;
break;
case MB_LOAD_CONFIG:
if(document.readyState){
config=new Config(mbConfigUrl);
config.loadConfigScripts();
}else{
this.setLoadState(MB_LOADED);
}
break;
case MB_LOADED:
window.clearInterval(this.scriptsTimer);
break;
}
};
this.loadScript=function(_2){
if(typeof MapBuilder_Release=="boolean"){
if(_2.indexOf(baseDir+"/util/")!=-1){
return;
}
var _3=_2.split("/");
var _4=_3[_3.length-1].replace(/.js$/,"");
if(typeof window[_4]=="function"){
return;
}
}
if(!document.getElementById(_2)){
var _5=document.createElement("script");
_5.src=_2;
_5.id=_2;
_5.defer=false;
_5.type="text/javascript";
if(document.readyState&&this.loadOrdered==true){
this.orderedScripts.push(_5);
if(!this.scriptLoader){
this.scriptLoader=window.setInterval("mapbuilder.loadNextScript()",50);
}
}else{
document.getElementsByTagName("head")[0].appendChild(_5);
}
if(document.readyState){
this.loadingScripts.push(_5);
}
}
};
this.loadNextScript=function(){
if(this.orderedScripts.length==0){
window.clearInterval(this.scriptLoader);
this.scriptLoader=null;
}else{
var _6=this.orderedScripts[0];
if(!_6.loading){
_6.loading=true;
this.doLoadScript(_6);
}
}
};
this.doLoadScript=function(_7){
var _8=this;
_7.onreadystatechange=function(){
_8.checkScriptState(this);
};
document.getElementsByTagName("head")[0].appendChild(_7);
};
this.checkScriptState=function(_9){
if(_9.readyState=="loaded"||_9.readyState=="complete"){
for(var i=0;i<this.orderedScripts.length;i++){
if(_9==this.orderedScripts[i]){
this.orderedScripts.splice(i,1);
break;
}
}
}
};
this.loadScriptsFromXpath=function(_b,_c){
for(var i=0;i<_b.length;i++){
if(_b[i].selectSingleNode("mb:scriptFile")==null){
scriptFile=baseDir+"/"+_c+_b[i].nodeName+".js";
this.loadScript(scriptFile);
}
}
};
if(!baseDir){
var _e=document.getElementsByTagName("head")[0];
var _f=_e.childNodes;
for(var i=0;i<_f.length;++i){
var src=_f.item(i).src;
if(src){
var _12=src.indexOf("/Mapbuilder.js");
if(_12>=0){
baseDir=src.substring(0,_12);
}else{
_12=src.indexOf("/MapbuilderCompressed.js");
if(_12>=0){
baseDir=src.substring(0,_12);
}
}
}
}
}
this.setLoadState(MB_LOAD_CORE);
this.scriptsTimer=window.setInterval("mapbuilder.checkScriptsLoaded()",100);
}
function checkIESecurity(){
var _13=["Msxml2.DOMDocument.5.0","Msxml2.DOMDocument.4.0","Msxml2.DOMDocument.3.0","MSXML2.DOMDocument","MSXML.DOMDocument","Microsoft.XMLDOM"];
var _14=false;
for(var i=0;i<_13.length&&!_14;i++){
try{
var _16=new ActiveXObject(_13[i]);
_14=true;
}
catch(e){
}
}
if(!_14){
window.open("/mapbuilder/docs/help/IESetup.html");
}
}
if(navigator.userAgent.toLowerCase().indexOf("msie")>-1){
checkIESecurity();
}
var mapbuilder=new Mapbuilder();
function mapbuilderInit(){
if(mapbuilder&&mapbuilder.loadState==MB_LOADED){
window.clearInterval(mbTimerId);
config.parseConfig(config);
if(Proj4js){
Proj4js.libPath=baseDir+"/util/proj4js/";
Proj4js.proxyScript=config.proxyUrl+"?url=";
}
config.callListeners("init");
var _17=new Date();
if(window.mbInit){
window.mbInit();
}
config.callListeners("loadModel");
}
}
var mbTimerId;
function mbDoLoad(_18){
mbTimerId=window.setInterval("mapbuilderInit()",100);
if(_18){
window.mbInit=_18;
}
}

Exception in thread "main" java.lang.ClassFormatError: Invalid method Code length 73789 in class file org/mozilla/javascript/gen/c1
	at java.lang.ClassLoader.defineClass1(Native Method)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:620)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:465)
	at org.mozilla.javascript.DefiningClassLoader.defineClass(DefiningClassLoader.java:57)
	at org.mozilla.javascript.optimizer.Codegen.defineClass(Codegen.java:122)
	at org.mozilla.javascript.optimizer.Codegen.createScriptObject(Codegen.java:77)
	at org.mozilla.javascript.Context.compileImpl(Context.java:2265)
	at org.mozilla.javascript.Context.compileString(Context.java:1314)
	at org.mozilla.javascript.Context.compileString(Context.java:1303)
	at org.mozilla.javascript.tools.shell.Main.loadScriptFromSource(Main.java:487)
	at org.mozilla.javascript.tools.shell.Main.processFileSecure(Main.java:403)
	at org.mozilla.javascript.tools.shell.Main.processFile(Main.java:369)
	at org.mozilla.javascript.tools.shell.Main.processSource(Main.java:360)
	at org.mozilla.javascript.tools.shell.Main.processFiles(Main.java:153)
	at org.mozilla.javascript.tools.shell.Main$IProxy.run(Main.java:83)
	at org.mozilla.javascript.Context.call(Context.java:528)
	at org.mozilla.javascript.ContextFactory.call(ContextFactory.java:447)
	at org.mozilla.javascript.tools.shell.Main.exec(Main.java:136)
	at org.mozilla.javascript.tools.shell.Main.main(Main.java:114)
function Sarissa(){
}
Sarissa.PARSED_OK="Document contains no parsing errors";
Sarissa.PARSED_EMPTY="Document is empty";
Sarissa.PARSED_UNKNOWN_ERROR="Not well-formed or other error";
var _sarissa_iNsCounter=0;
var _SARISSA_IEPREFIX4XSLPARAM="";
var _SARISSA_HAS_DOM_IMPLEMENTATION=document.implementation&&true;
var _SARISSA_HAS_DOM_CREATE_DOCUMENT=_SARISSA_HAS_DOM_IMPLEMENTATION&&document.implementation.createDocument;
var _SARISSA_HAS_DOM_FEATURE=_SARISSA_HAS_DOM_IMPLEMENTATION&&document.implementation.hasFeature;
var _SARISSA_IS_MOZ=_SARISSA_HAS_DOM_CREATE_DOCUMENT&&_SARISSA_HAS_DOM_FEATURE;
var _SARISSA_IS_SAFARI=(navigator.userAgent&&navigator.vendor&&(navigator.userAgent.toLowerCase().indexOf("applewebkit")!=-1||navigator.vendor.indexOf("Apple")!=-1));
var _SARISSA_IS_OPERA=navigator.userAgent.toLowerCase().indexOf("opera")!=-1;
var _SARISSA_IS_IE=document.all&&window.ActiveXObject&&navigator.userAgent.toLowerCase().indexOf("msie")>-1&&navigator.userAgent.toLowerCase().indexOf("opera")==-1;
if(!window.Node||!Node.ELEMENT_NODE){
Node={ELEMENT_NODE:1,ATTRIBUTE_NODE:2,TEXT_NODE:3,CDATA_SECTION_NODE:4,ENTITY_REFERENCE_NODE:5,ENTITY_NODE:6,PROCESSING_INSTRUCTION_NODE:7,COMMENT_NODE:8,DOCUMENT_NODE:9,DOCUMENT_TYPE_NODE:10,DOCUMENT_FRAGMENT_NODE:11,NOTATION_NODE:12};
}
if(typeof XMLDocument=="undefined"&&typeof Document!="undefined"){
XMLDocument=Document;
}
if(_SARISSA_IS_IE){
_SARISSA_IEPREFIX4XSLPARAM="xsl:";
var _SARISSA_DOM_PROGID="";
var _SARISSA_XMLHTTP_PROGID="";
var _SARISSA_DOM_XMLWRITER="";
Sarissa.pickRecentProgID=function(_1){
var _2=false;
for(var i=0;i<_1.length&&!_2;i++){
try{
var _4=new ActiveXObject(_1[i]);
o2Store=_1[i];
_2=true;
}
catch(objException){
}
}
if(!_2){
throw "Could not retreive a valid progID of Class: "+_1[_1.length-1]+". (original exception: "+e+")";
}
_1=null;
return o2Store;
};
_SARISSA_DOM_PROGID=null;
_SARISSA_THREADEDDOM_PROGID=null;
_SARISSA_XSLTEMPLATE_PROGID=null;
_SARISSA_XMLHTTP_PROGID=null;
if(!window.XMLHttpRequest){
XMLHttpRequest=function(){
if(!_SARISSA_XMLHTTP_PROGID){
_SARISSA_XMLHTTP_PROGID=Sarissa.pickRecentProgID(["Msxml2.XMLHTTP.6.0","MSXML2.XMLHTTP.3.0","MSXML2.XMLHTTP","Microsoft.XMLHTTP"]);
}
return new ActiveXObject(_SARISSA_XMLHTTP_PROGID);
};
}
Sarissa.getDomDocument=function(_5,_6){
if(!_SARISSA_DOM_PROGID){
_SARISSA_DOM_PROGID=Sarissa.pickRecentProgID(["Msxml2.DOMDocument.6.0","Msxml2.DOMDocument.3.0","MSXML2.DOMDocument","MSXML.DOMDocument","Microsoft.XMLDOM"]);
}
var _7=new ActiveXObject(_SARISSA_DOM_PROGID);
if(_6){
var _8="";
if(_5){
if(_6.indexOf(":")>1){
_8=_6.substring(0,_6.indexOf(":"));
_6=_6.substring(_6.indexOf(":")+1);
}else{
_8="a"+(_sarissa_iNsCounter++);
}
}
if(_5){
_7.loadXML("<"+_8+":"+_6+" xmlns:"+_8+"=\""+_5+"\""+" />");
}else{
_7.loadXML("<"+_6+" />");
}
}
return _7;
};
Sarissa.getParseErrorText=function(_9){
var _a=Sarissa.PARSED_OK;
if(_9.parseError.errorCode!=0){
_a="XML Parsing Error: "+_9.parseError.reason+"\nLocation: "+_9.parseError.url+"\nLine Number "+_9.parseError.line+", Column "+_9.parseError.linepos+":\n"+_9.parseError.srcText+"\n";
for(var i=0;i<_9.parseError.linepos;i++){
_a+="-";
}
_a+="^\n";
}else{
if(_9.documentElement==null){
_a=Sarissa.PARSED_EMPTY;
}
}
return _a;
};
Sarissa.setXpathNamespaces=function(_c,_d){
_c.setProperty("SelectionLanguage","XPath");
_c.setProperty("SelectionNamespaces",_d);
};
XSLTProcessor=function(){
if(!_SARISSA_XSLTEMPLATE_PROGID){
_SARISSA_XSLTEMPLATE_PROGID=Sarissa.pickRecentProgID(["Msxml2.XSLTemplate.6.0","MSXML2.XSLTemplate.3.0"]);
}
this.template=new ActiveXObject(_SARISSA_XSLTEMPLATE_PROGID);
this.processor=null;
};
XSLTProcessor.prototype.importStylesheet=function(_e){
if(!_SARISSA_THREADEDDOM_PROGID){
_SARISSA_THREADEDDOM_PROGID=Sarissa.pickRecentProgID(["MSXML2.FreeThreadedDOMDocument.6.0","MSXML2.FreeThreadedDOMDocument.3.0"]);
}
_e.setProperty("SelectionLanguage","XPath");
_e.setProperty("SelectionNamespaces","xmlns:xsl='http://www.w3.org/1999/XSL/Transform'");
var _f=new ActiveXObject(_SARISSA_THREADEDDOM_PROGID);
if(_e.url&&_e.selectSingleNode("//xsl:*[local-name() = 'import' or local-name() = 'include']")!=null){
_f.async=false;
if(_SARISSA_THREADEDDOM_PROGID=="MSXML2.FreeThreadedDOMDocument.6.0"){
_f.setProperty("AllowDocumentFunction",true);
_f.resolveExternals=true;
}
_f.load(_e.url);
}else{
_f.loadXML(_e.xml);
}
_f.setProperty("SelectionNamespaces","xmlns:xsl='http://www.w3.org/1999/XSL/Transform'");
var _10=_f.selectSingleNode("//xsl:output");
this.outputMethod=_10?_10.getAttribute("method"):"html";
this.template.stylesheet=_f;
this.processor=this.template.createProcessor();
this.paramsSet=new Array();
};
XSLTProcessor.prototype.transformToDocument=function(_11){
if(_SARISSA_THREADEDDOM_PROGID){
this.processor.input=_11;
var _12=new ActiveXObject(_SARISSA_DOM_PROGID);
this.processor.output=_12;
this.processor.transform();
return _12;
}else{
if(!_SARISSA_DOM_XMLWRITER){
_SARISSA_DOM_XMLWRITER=Sarissa.pickRecentProgID(["Msxml2.MXXMLWriter.6.0","Msxml2.MXXMLWriter.3.0","MSXML2.MXXMLWriter","MSXML.MXXMLWriter","Microsoft.XMLDOM"]);
}
this.processor.input=_11;
var _12=new ActiveXObject(_SARISSA_DOM_XMLWRITER);
this.processor.output=_12;
this.processor.transform();
var _13=new ActiveXObject(_SARISSA_DOM_PROGID);
_13.loadXML(_12.output+"");
return _13;
}
};
XSLTProcessor.prototype.transformToFragment=function(_14,_15){
this.processor.input=_14;
this.processor.transform();
var s=this.processor.output;
var f=_15.createDocumentFragment();
if(this.outputMethod=="text"){
f.appendChild(_15.createTextNode(s));
}else{
if(_15.body&&_15.body.innerHTML){
var _18=_15.createElement("div");
_18.innerHTML=s;
while(_18.hasChildNodes()){
f.appendChild(_18.firstChild);
}
}else{
var _19=new ActiveXObject(_SARISSA_DOM_PROGID);
if(s.substring(0,5)=="<?xml"){
s=s.substring(s.indexOf("?>")+2);
}
var xml="".concat("<my>",s,"</my>");
_19.loadXML(xml);
var _18=_19.documentElement;
while(_18.hasChildNodes()){
f.appendChild(_18.firstChild);
}
}
}
return f;
};
XSLTProcessor.prototype.setParameter=function(_1b,_1c,_1d){
if(_1b){
this.processor.addParameter(_1c,_1d,_1b);
}else{
this.processor.addParameter(_1c,_1d);
}
if(!this.paramsSet[""+_1b]){
this.paramsSet[""+_1b]=new Array();
}
this.paramsSet[""+_1b][_1c]=_1d;
};
XSLTProcessor.prototype.getParameter=function(_1e,_1f){
_1e=_1e||"";
if(this.paramsSet[_1e]&&this.paramsSet[_1e][_1f]){
return this.paramsSet[_1e][_1f];
}else{
return null;
}
};
XSLTProcessor.prototype.clearParameters=function(){
for(var _20 in this.paramsSet){
for(var _21 in this.paramsSet[_20]){
if(_20){
this.processor.addParameter(_21,null,_20);
}else{
this.processor.addParameter(_21,null);
}
}
}
this.paramsSet=new Array();
};
}else{
if(_SARISSA_HAS_DOM_CREATE_DOCUMENT){
Sarissa.__handleLoad__=function(_22){
Sarissa.__setReadyState__(_22,4);
};
_sarissa_XMLDocument_onload=function(){
Sarissa.__handleLoad__(this);
};
Sarissa.__setReadyState__=function(_23,_24){
_23.readyState=_24;
_23.readystate=_24;
if(_23.onreadystatechange!=null&&typeof _23.onreadystatechange=="function"){
_23.onreadystatechange();
}
};
Sarissa.getDomDocument=function(_25,_26){
var _27=document.implementation.createDocument(_25?_25:null,_26?_26:null,null);
if(!_27.onreadystatechange){
_27.onreadystatechange=null;
}
if(!_27.readyState){
_27.readyState=0;
}
_27.addEventListener("load",_sarissa_XMLDocument_onload,false);
return _27;
};
if(window.XMLDocument){
}else{
if(_SARISSA_HAS_DOM_FEATURE&&window.Document&&!Document.prototype.load&&document.implementation.hasFeature("LS","3.0")){
Sarissa.getDomDocument=function(_28,_29){
var _2a=document.implementation.createDocument(_28?_28:null,_29?_29:null,null);
return _2a;
};
}else{
Sarissa.getDomDocument=function(_2b,_2c){
var _2d=document.implementation.createDocument(_2b?_2b:null,_2c?_2c:null,null);
if(_2d&&(_2b||_2c)&&!_2d.documentElement){
_2d.appendChild(_2d.createElementNS(_2b,_2c));
}
return _2d;
};
}
}
}
}
if(!window.DOMParser){
if(_SARISSA_IS_SAFARI){
DOMParser=function(){
};
DOMParser.prototype.parseFromString=function(_2e,_2f){
var _30=new XMLHttpRequest();
_30.open("GET","data:text/xml;charset=utf-8,"+encodeURIComponent(_2e),false);
_30.send(null);
return _30.responseXML;
};
}else{
if(Sarissa.getDomDocument&&Sarissa.getDomDocument()&&Sarissa.getDomDocument(null,"bar").xml){
DOMParser=function(){
};
DOMParser.prototype.parseFromString=function(_31,_32){
var doc=Sarissa.getDomDocument();
doc.loadXML(_31);
return doc;
};
}
}
}
if((typeof (document.importNode)=="undefined")&&_SARISSA_IS_IE){
try{
document.importNode=function(_34,_35){
var tmp;
if(_34.nodeName=="tbody"||_34.nodeName=="tr"){
tmp=document.createElement("table");
}else{
if(_34.nodeName=="td"){
tmp=document.createElement("tr");
}else{
if(_34.nodeName=="option"){
tmp=document.createElement("select");
}else{
tmp=document.createElement("div");
}
}
}
if(_35){
tmp.innerHTML=_34.xml?_34.xml:_34.outerHTML;
}else{
tmp.innerHTML=_34.xml?_34.cloneNode(false).xml:_34.cloneNode(false).outerHTML;
}
return tmp.getElementsByTagName("*")[0];
};
}
catch(e){
}
}
if(!Sarissa.getParseErrorText){
Sarissa.getParseErrorText=function(_37){
var _38=Sarissa.PARSED_OK;
if(!_37.documentElement){
_38=Sarissa.PARSED_EMPTY;
}else{
if(_37.documentElement.tagName=="parsererror"){
_38=_37.documentElement.firstChild.data;
_38+="\n"+_37.documentElement.firstChild.nextSibling.firstChild.data;
}else{
if(_37.getElementsByTagName("parsererror").length>0){
var _39=_37.getElementsByTagName("parsererror")[0];
_38=Sarissa.getText(_39,true)+"\n";
}else{
if(_37.parseError&&_37.parseError.errorCode!=0){
_38=Sarissa.PARSED_UNKNOWN_ERROR;
}
}
}
}
return _38;
};
}
Sarissa.getText=function(_3a,_3b){
var s="";
var _3d=_3a.childNodes;
for(var i=0;i<_3d.length;i++){
var _3f=_3d[i];
var _40=_3f.nodeType;
if(_40==Node.TEXT_NODE||_40==Node.CDATA_SECTION_NODE){
s+=_3f.data;
}else{
if(_3b==true&&(_40==Node.ELEMENT_NODE||_40==Node.DOCUMENT_NODE||_40==Node.DOCUMENT_FRAGMENT_NODE)){
s+=Sarissa.getText(_3f,true);
}
}
}
return s;
};
if(!window.XMLSerializer&&Sarissa.getDomDocument&&Sarissa.getDomDocument("","foo",null).xml){
XMLSerializer=function(){
};
XMLSerializer.prototype.serializeToString=function(_41){
return _41.xml;
};
}
Sarissa.stripTags=function(s){
return s.replace(/<[^>]+>/g,"");
};
Sarissa.clearChildNodes=function(_43){
while(_43.firstChild){
_43.removeChild(_43.firstChild);
}
};
Sarissa.copyChildNodes=function(_44,_45,_46){
if((!_44)||(!_45)){
throw "Both source and destination nodes must be provided";
}
if(!_46){
Sarissa.clearChildNodes(_45);
}
var _47=_45.nodeType==Node.DOCUMENT_NODE?_45:_45.ownerDocument;
var _48=_44.childNodes;
if(typeof (_47.importNode)!="undefined"){
for(var i=0;i<_48.length;i++){
_45.appendChild(_47.importNode(_48[i],true));
}
}else{
for(var i=0;i<_48.length;i++){
_45.appendChild(_48[i].cloneNode(true));
}
}
};
Sarissa.moveChildNodes=function(_4a,_4b,_4c){
if((!_4a)||(!_4b)){
throw "Both source and destination nodes must be provided";
}
if(!_4c){
Sarissa.clearChildNodes(_4b);
}
var _4d=_4a.childNodes;
if(_4a.ownerDocument==_4b.ownerDocument){
while(_4a.firstChild){
_4b.appendChild(_4a.firstChild);
}
}else{
var _4e=_4b.nodeType==Node.DOCUMENT_NODE?_4b:_4b.ownerDocument;
if(typeof (_4e.importNode)!="undefined"){
for(var i=0;i<_4d.length;i++){
_4b.appendChild(_4e.importNode(_4d[i],true));
}
}else{
for(var i=0;i<_4d.length;i++){
_4b.appendChild(_4d[i].cloneNode(true));
}
}
Sarissa.clearChildNodes(_4a);
}
};
Sarissa.xmlize=function(_50,_51,_52){
_52=_52?_52:"";
var s=_52+"<"+_51+">";
var _54=false;
if(!(_50 instanceof Object)||_50 instanceof Number||_50 instanceof String||_50 instanceof Boolean||_50 instanceof Date){
s+=Sarissa.escape(""+_50);
_54=true;
}else{
s+="\n";
var _55="";
var _56=_50 instanceof Array;
for(var _57 in _50){
s+=Sarissa.xmlize(_50[_57],(_56?"array-item key=\""+_57+"\"":_57),_52+"   ");
}
s+=_52;
}
return s+=(_51.indexOf(" ")!=-1?"</array-item>\n":"</"+_51+">\n");
};
Sarissa.escape=function(_58){
return _58.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;").replace(/'/g,"&apos;");
};
Sarissa.unescape=function(_59){
return _59.replace(/&apos;/g,"'").replace(/&quot;/g,"\"").replace(/&gt;/g,">").replace(/&lt;/g,"<").replace(/&amp;/g,"&");
};

IS_SAFARI=navigator.userAgent.toLowerCase().indexOf("safari")!=-1||navigator.userAgent.toLowerCase().indexOf("konqueror")!=-1;
IS_SAFARI_OLD=IS_SAFARI&&parseInt((navigator.userAgent.match(/AppleWebKit\/(\d+)/)||{})[1])<420;
IS_GECKO=document.implementation&&document.implementation.createDocument&&true;
IS_OPERA=navigator.userAgent.toLowerCase().indexOf("opera")!=-1;
IS_IE=document.all&&!IS_OPERA;
IS_NEW_SAFARI=IS_SAFARI&&navigator.userAgent.toLowerCase().indexOf("version/3")!=-1;
if(IS_SAFARI_OLD&&!IS_NEW_SAFARI){
HTMLHtmlElement=document.createElement("html").constructor;
Node=HTMLElement={};
HTMLElement.prototype=HTMLHtmlElement.__proto__.__proto__;
HTMLDocument=Document=document.constructor;
var x=new DOMParser();
XMLDocument=x.constructor;
Element=x.parseFromString("<Single />","text/xml").documentElement.constructor;
x=null;
}
TAGNAME=IS_IE?"baseName":"localName";
var _$=function(_1,_2,_3,_4){
return (_2||document).getElementsByTagName(_1);
};
if(IS_SAFARI){
XPath={cache:{},getChildNode:function(_5,_6,_7,_8,_9,_a){
var _b=0,result=null,data=_7[_8];
if(_6!=null){
if(_6.indexOf(":")>0){
_6=_6.split(":")[1];
}
}
var _c=_5.childNodes;
if(!_c){
return;
}
for(var i=0;i<_c.length;i++){
if(_6&&(_c[i].style?_c[i].tagName.toLowerCase():_c[i].tagName)!=_6){
continue;
}
if(data){
data[0](_c[i],data[1],_7,_8+1,_b++,_a);
}else{
_a.push(_c[i]);
}
}
},doQuery:function(_e,_f,_10,_11,num,_13){
var _14=null,data=_10[_11];
var _15=_f[0];
var _16=_f[1];
try{
var _17=eval(_15);
}
catch(e){
alert("eror :"+e+" in query : "+_15);
return;
}
if(_16){
return _13.push(_17);
}
if(_17==null||_17=="undefined"||_17==""){
return;
}
if(data){
data[0](_e,data[1],_10,_11+1,0,_13);
}else{
_13.push(_e);
}
},getTextNode:function(_18,_19,_1a,_1b,num,_1d){
var _1e=null,data=_1a[_1b];
var _1f=_18.childNodes;
for(var i=0;i<_1f.length;i++){
if(_1f[i].nodeType!=3&&_1f[i].nodeType!=4){
continue;
}
if(data){
data[0](_1f[i],data[1],_1a,_1b+1,i,_1d);
}else{
_1d.push(_1f[i]);
}
}
},getAnyNode:function(_21,_22,_23,_24,num,_26){
var _27=null,data=_23[_24];
var sel=[],nodes=_21.childNodes;
for(var i=0;i<nodes.length;i++){
if(data){
data[0](nodes[i],data[1],_23,_24+1,i,_26);
}else{
_26.push(nodes[i]);
}
}
},getAttributeNode:function(_2a,_2b,_2c,_2d,num,_2f){
if(!_2a||_2a.nodeType!=1){
return;
}
var _30=null,data=_2c[_2d];
var _31=_2a.getAttributeNode(_2b);
if(data){
data[0](_31,data[1],_2c,_2d+1,0,_2f);
}else{
if(_31){
_2f.push(_31);
}
}
},getAllNodes:function(_32,x,_34,_35,num,_37){
var _38=null,data=_34[_35];
var _39=x[0];
if(_39!=null){
if(_39.indexOf(":")>0){
_39=_39.split(":")[1];
}
}
var _3a=x[1];
var _3b=x[2];
if(_3a&&(_32.tagName==_39||_39=="*")){
if(data){
data[0](_32,data[1],_34,_35+1,0,_37);
}else{
_37.push(_32);
}
}
var _3c=_$(_39,_32,_39==_3b?"":_3b);
for(var i=0;i<_3c.length;i++){
if(data){
data[0](_3c[i],data[1],_34,_35+1,i,_37);
}else{
_37.push(_3c[i]);
}
}
},getParentNode:function(_3e,_3f,_40,_41,num,_43){
var _44=null,data=_40[_41];
var _45=_3e.parentNode;
if(data){
data[0](_45,data[1],_40,_41+1,0,_43);
}else{
if(_45){
_43.push(_45);
}
}
},getPrecedingSibling:function(_46,_47,_48,_49,num,_4b){
if(_47=="*"){
_47=_46.tagName;
}
var _4c=null,data=_48[_49];
var _4d=_46.previousSibling;
while(_4d){
if(_47!="node()"&&_4d.tagName!=_47){
continue;
}
if(_4d){
_4b.push(_4d);
}
_4d=_4d.previousSibling;
}
},getFollowingSibling:function(_4e,_4f,_50,_51,num,_53){
var _54=null,data=_50[_51];
if(_4f=="*"){
_4f=_4e.tagName;
}
var _55=_4e.nextSibling;
while(_55){
if(_4f!="NODE()"&&_55.tagName!=_4f){
continue;
}
if(_55!=null){
_53.push(_55);
}
_55=_55.previousSibling;
}
},getPreceding:function(_56,_57,_58,_59,num,_5b){
if(_57=="*"){
_57=_56.tagName;
}
var _5c=null,data=_58[_59];
var _5d=_56.parentNode;
var _5e=_56.previousSibling;
while(_5e){
if(_5e.parentNode!=_5d&&_57!="NODE()"&&_5e.tagName!=_57){
_5e=_5e.previousSibling;
continue;
}
if(_5e){
_5b.push(_5e);
_5e=_5e.previousSibling;
}
}
},getFollowing:function(_5f,_60,_61,_62,num,_64){
if(_60=="*"){
_60=_5f.tagName;
}
var _65=null,data=_61[_62];
var _66=_5f.parentNode;
var _67=_5f.nextSibling;
while(_67){
if(_67.parentNode!=_66&&_60!="NODE()"&&_67.tagName!=_60){
_67=_67.nextSibling;
continue;
}
if(_67){
_64.push(_67);
_67=_67.nextSibling;
}
}
},multiXpaths:function(_68,_69,_6a,_6b,num,_6d){
for(var i=_69.length;i>0;i--){
var _6a=_69[i][0];
var _6f=(_6a[3]?_68.ownerDocument.documentElement:_68);
_6a[0](_6f,_6a[1],_69[i],1,0,_6d);
}
_6d.makeUnique();
},compile:function(_70){
_70=_70.replace(/\[(\d+)\]/g,"/##$1");
_70=_70.replace(/\|\|(\d+)\|\|\d+/g,"##$1");
_70=_70.replace(/\.\|\|\d+/g,".");
_70=_70.replace(/\[([^\]]*)\]/g,"/##$1#");
if(_70=="/"||_70=="."){
return _70;
}
_70=_70.replace(/\/\//g,"descendant::");
return this.processXpath(_70);
},processXpath:function(_71){
var _72=new Array();
_71=_71.replace(/('[^']*)\|([^']*')/g,"$1_@_$2");
_71=_71.split("|");
for(var i=0;i<_71.length;i++){
_71[i]=_71[i].replace(/('[^']*)\_\@\_([^']*')/g,"$1|$2");
}
if(_71.length==1){
_71=_71[0];
}else{
for(var i=0;i<_71.length;i++){
_71[i]=this.processXpath(_71[i]);
}
_72.push([this.multiXpaths,_71]);
return _72;
}
if(_71.match(/\(/)){
_72.push([this.doQuery,[this.compileQuery(_71),true]]);
return _72;
}
var _74=_71.match(/^\/[^\/]/);
var _75=_71.split("/");
for(var i=0;i<_75.length;i++){
if(_75[i]=="."||_75[i]==""){
continue;
}else{
if(_75[i].match(/^[\w-_\.]+(?:\:[\w-_\.]+){0,1}$/)){
_72.push([this.getChildNode,_75[i]]);
}else{
if(_75[i].match(/^\#\#(\d+)$/)){
_72.push([this.doQuery,["num+1 == "+parseInt(RegExp.$1)]]);
}else{
if(_75[i].match(/^\#\#(.*)$/)){
var _76=RegExp.$1;
var m=[_76.match(/\(/g),_76.match(/\)/g)];
if(m[0]||m[1]){
while(!m[0]&&m[1]||m[0]&&!m[1]||m[0].length!=m[1].length){
if(!_75[++i]){
break;
}
_76+=_75[i];
}
}else{
i++;
while(_75[i]){
if(!_75[i]){
break;
}
if(_75[i-1].match(/.*\#$/)){
break;
}
_76+="/"+_75[i];
i++;
}
}
_76=_76.replace(/\#/,"");
_72.push([this.doQuery,[this.compileQuery(_76)]]);
}else{
if(_75[i]=="*"){
_72.push([this.getChildNode,null]);
}else{
if(_75[i].substr(0,2)=="[]"){
_72.push([this.getAllNodes,["*",false]]);
}else{
if(_75[i].match(/::/)){
if(_75[i].match(/descendant-or-self::node\(\)$/)){
_72.push([this.getAllNodes,["*",true]]);
}else{
if(_75[i].match(/descendant-or-self::([^\:]*)(?:\:(.*)){0,1}$/)){
_72.push([this.getAllNodes,[RegExp.$2||RegExp.$1,true,RegExp.$1]]);
}else{
if(_75[i].match(/descendant::([^\:]*)(?:\:(.*)){0,1}$/)){
_72.push([this.getAllNodes,[RegExp.$2||RegExp.$1,false,RegExp.$1]]);
}else{
if(_75[i].match(/following-sibling::(.*)$/)){
_72.push([this.getFollowingSibling,RegExp.$1.toUpperCase()]);
}else{
if(_75[i].match(/preceding-sibling::(.*)$/)){
_72.push([this.getPrecedingSibling,RegExp.$1.toUpperCase()]);
}else{
if(_75[i].match(/following::(.*)$/)){
_72.push([this.getFollowing,RegExp.$1.toUpperCase()]);
}else{
if(_75[i].match(/preceding::(.*)$/)){
_72.push([this.getPreceding,RegExp.$1.toUpperCase()]);
}else{
if(_75[i].match(/self::(.*)$/)){
_72.push([this.doQuery,["XPath.doXpathFunc('local-name', htmlNode) == '"+RegExp.$1+"'"]]);
}
}
}
}
}
}
}
}
}else{
if(_75[i].match(/^\@(.*)$/)){
_72.push([this.getAttributeNode,RegExp.$1]);
}else{
if(_75[i]=="text()"){
_72.push([this.getTextNode,null]);
}else{
if(_75[i]=="node()"){
_72.push([this.getAnyNode,null]);
}else{
if(_75[i]==".."){
_72.push([this.getParentNode,null]);
}else{
var _76=_75[i];
var m=[_76.match(/\(/g),_76.match(/\)/g)];
if(m[0]||m[1]){
while(!m[0]&&m[1]||m[0]&&!m[1]||m[0].length!=m[1].length){
if(!_75[++i]){
break;
}
_76+="/"+_75[i];
m=[_76.match(/\(/g),_76.match(/\)/g)];
}
}
_72.push([this.doQuery,[this.compileQuery(_76),true]]);
}
}
}
}
}
}
}
}
}
}
}
}
_72[0][3]=_74;
return _72;
},compileQuery:function(_78){
var c=new CodeCompilation(_78);
var _7a=c.compile();
return _7a;
},doXpathFunc:function(_7b,_7c,_7d,_7e){
switch(_7b){
case "not":
if((_7c==null)||(_7c==true)){
return false;
}else{
return true;
}
case "position":
return parseInt(_7c);
case "position()":
return num;
case "format-number":
return new String(Math.round(parseFloat(_7c)*100)/100).replace(/(\.\d?\d?)$/,function(m1){
return m1.pad(3,"0",PAD_RIGHT);
});
case "floor":
return Math.floor(_7c);
case "ceiling":
return Math.ceil(_7c);
case "starts-with":
return _7c?_7c.substr(0,_7d.length)==_7d:false;
case "string-length":
return _7c?_7c.length:0;
case "count":
return _7c?parseInt(_7c.length):0;
case "last":
return _7c?_7c[_7c.length-1]:null;
case "local-name":
return _7c?_7c.tagName:"";
case "substring":
return _7c&&_7d?_7c.substring(_7d,_7e||0):"";
case "contains":
return _7c&&_7d?_7c.indexOf(_7d)>-1:false;
case "concat":
for(var str="",i=1;i<arguments.length;i++){
if(typeof arguments[i]=="object"){
str+=getNodeValue(arguments[i][0]);
continue;
}
str+=arguments[i];
}
return str;
}
},selectNodeExtended:function(_81,_82){
var _83=this.selectNodes(_81,_82);
if(_83.length==1){
_83=_83[0];
return getNodeValue(_83);
}
return _83;
},selectNodes:function(_84,_85){
if(!this.cache[_84]){
this.cache[_84]=this.compile(_84);
}
if(typeof this.cache[_84]=="string"){
if(this.cache[_84]=="."){
return [_85];
}
if(this.cache[_84]=="/"){
return [_85.nodeType==9?_85:_85.ownerDocument.documentElement];
}
}
var _86=this.cache[_84][0];
var _87=(_86[3]&&!_85.nodeType==9?_85.ownerDocument.documentElement:_85);
var _88=[];
var y=new XMLSerializer();
_86[0](_87,_86[1],this.cache[_84],1,0,_88);
if(_88==""){
if(_84.indexOf(/\/\//)){
while(_87.parentNode){
if(_87){
_87=_87.parentNode;
}
}
_86[0](_87,_86[1],this.cache[_84],1,0,_88);
}
}
return _88;
}};
function getNodeValue(_8a){
if(_8a.nodeType==1){
return _8a.firstChild?_8a.firstChild.nodeValue:"";
}
if(_8a.nodeType>1||_8a.nodeType<5){
return _8a.nodeValue;
}
return _8a;
}
function CodeCompilation(_8b){
this.data={F:[],S:[],I:[],X:[],P:[]};
this.compile=function(){
_8b=_8b.replace(/ or /g," || ");
_8b=_8b.replace(/ and /g," && ");
_8b=_8b.replace(/!=/g,"{}");
_8b=_8b.replace(/ mod /g,"%");
_8b=_8b.replace(/=/g,"==");
_8b=_8b.replace(/====/g,"==");
_8b=_8b.replace(/\{\}/g,"!=");
this.tokenize();
this.insert();
return _8b;
};
this.tokenize=function(){
var _8c=this.data.P;
_8b=_8b.replace(/\((.*\#\#.*[^)])/g,function(d,_8e){
return "("+(_8c.push(_8e)-1)+"P_";
});
var _8c=this.data.F;
_8b=_8b.replace(/(format-number|contains|substring|local-name|last|node|position|round|starts-with|string|string-length|sum|floor|ceiling|concat|count|not)\s*\(/g,function(d,_90){
return (_8c.push(_90)-1)+"F_";
});
var _8c=this.data.S;
_8b=_8b.replace(/'([^']*)'/g,function(d,_92){
return (_8c.push(_92)-1)+"S_";
});
_8b=_8b.replace(/"([^"]*)"/g,function(d,_94){
return (_8c.push(_94)-1)+"S_";
});
var _8c=this.data.X;
_8b=_8b.replace(/(^|\W|\_)([\@\.\/A-Za-z][\.\@\/\w:-]*(?:\(\)){0,1})/g,function(d,m1,m2){
return m1+(_8c.push(m2)-1)+"X_";
});
_8b=_8b.replace(/(\.[\.\@\/\w]*)/g,function(d,m1,m2){
return (_8c.push(m1)-1)+"X_";
});
var _8c=this.data.I;
_8b=_8b.replace(/(\d+)(\W)/g,function(d,m1,m2){
return (_8c.push(m1)-1)+"I_"+m2;
});
};
this.insert=function(){
var _9e=this.data;
var _9f=0;
_8b=_8b.replace(/(\d+)([FISXP])_/g,function(d,nr,_a2){
var _a3=_9e[_a2][nr];
if(_a2=="F"){
_9f++;
var _a4="XPath.doXpathFunc('"+_a3+"', ";
if(_a3=="position"){
_a4+="XPath.selectNodes('count(preceding::'+htmlNode.tagName+')', htmlNode)";
}
return _a4;
}else{
if(_a2=="S"){
return "'"+_a3+"'";
}else{
if(_a2=="I"){
return _a3;
}else{
if(_a2=="X"){
if(_8b.indexOf("X")>_8b.indexOf(")")){
_9f=0;
}
if(_9f>0){
return "XPath.selectNodes('"+_a3.replace(/'/g,"\\'")+"', htmlNode)";
}else{
return "XPath.selectNodeExtended('"+_a3.replace(/'/g,"\\'")+"', htmlNode)";
}
}else{
if(_a2=="P"){
return "XPath.selectNodes('"+_a3.replace(/'/g,"\\'")+" ', htmlNode)";
}
}
}
}
}
});
};
}
}
if(IS_SAFARI){
HTMLDocument.prototype.selectNodes=Element.prototype.selectNodes=XMLDocument.prototype.selectNodes=function(_a5,_a6){
return XPath.selectNodes(_a5,_a6||this);
};
HTMLDocument.prototype.selectSingleNode=Element.prototype.selectSingleNode=XMLDocument.prototype.selectSingleNode=function(_a7,_a8){
return XPath.selectNodes(_a7,_a8||this)[0];
};
}

if(!IS_IE&&!self.XSLTProcessor){
XSLTProcessor=_Javeline_XSLTProcessor;
}
function _Javeline_XSLTProcessor(){
this.templates={};
this.paramsSet={};
this.currentTemplate="global";
this.p={"value-of":function(_1,_2,_3,_4){
var _5=this.lookforVariable(_2.getAttribute("select"));
var _6=_1.selectNodes(_5)[0];
if(_6!=null){
if(_6==null){
value="";
}else{
if(_6.nodeType==1){
value=_6.firstChild?_6.firstChild.nodeValue:"";
}else{
value=typeof _6=="object"?_6.nodeValue:_6;
}
}
}else{
value=_5;
}
_4.appendChild(this.xmlDoc.createTextNode(value));
},"copy-of":function(_7,_8,_9,_a){
var _b=XPath.selectNodes(this.lookforVariable(_8.getAttribute("select")),_7)[0];
if(_b){
_a.appendChild(!IS_IE?_a.ownerDocument.importNode(_b,true):_b.cloneNode(true));
}
},"if":function(_c,_d,_e,_f){
var _10=this.lookforVariable(_d.getAttribute("test"));
if(XPath.selectNodes(_10,_c)[0]){
this.parseChildren(_c,_d,_e,_f);
}
},"for-each":function(_11,_12,_13,_14){
var _15=XPath.selectNodes(this.lookforVariable(_12.getAttribute("select")),_11);
for(var i=0;i<_15.length;i++){
this.parseChildren(_15[i],_12,_13,_14);
}
},"attribute":function(_17,_18,_19,_1a){
var _1b=this.xmlDoc.createDocumentFragment();
this.parseChildren(_17,_18,_19,_1b);
_1a.setAttribute(_18.getAttribute("name"),_1b.xml);
},"choose":function(_1c,_1d,_1e,_1f){
var _20=_1d.childNodes;
for(var i=0;i<_20.length;i++){
if(!_20[i].tagName){
continue;
}
if(_20[i][TAGNAME]=="when"){
var _22=_1c.selectNodes(this.lookforVariable(_20[i].getAttribute("test")))[0];
if(_22=="undefined"){
return;
}
}
if(_20[i][TAGNAME]=="otherwise"||(_20[i][TAGNAME]=="when"&&_22)){
return this.parseChildren(_1c,_20[i],_1e[i][2],_1f);
}
}
},"text":function(_23,_24,_25,_26){
if(xmlNode==null){
value="";
}else{
if(xmlNode.nodeType==1){
value=xmlNode.firstChild?xmlNode.firstChild.nodeValue:"";
}else{
value=typeof xmlNode=="object"?xmlNode.nodeValue:xmlNode;
}
}
value=expression;
_26.appendChild(this.xmlDoc.createTextNode(value));
},"call-template":function(_27,_28,_29,_2a){
var t=this.templates[_28.getAttribute("name")];
this.currentTemplate=t;
if(t){
this.parseChildren(_27,t[0],t[1],_2a);
this.withparams(_28);
this.currentTemplate=t;
this.paramsSet[this.currentTemplate]=new Array();
}
},"apply-templates":function(_2c,_2d,_2e,_2f){
if(!_2d){
var _30=_2c.childNodes[0];
var _31=_30.tagName;
var _32=this.lookforTemplate(_31);
if(_32==0){
_32="/";
}
t=this.templates[_32];
if(t){
this.p["apply-templates"].call(this,_2c,t[0],t[1],_2f);
}
}else{
var aux=_2d.getAttribute("match")||_2d.getAttribute("select");
aux=this.lookforVariable(aux);
var _34="";
if(this.templates[aux]){
_34=aux;
}else{
_34=this.lookforTemplate2(aux);
}
if(aux){
this.currentTemplate=_34;
var t=this.templates[_34];
if(t){
var _36=_2c.selectNodes(aux);
var _37;
this.withparams(_2d);
if(_2d.firstChild[TAGNAME]=="sort"){
this.xslSort(_2d.firstChild,_36,_37);
}
if(!_36[0]){
return;
}
for(var i=0;i<_36.length;i++){
if(_2d.firstChild[TAGNAME]=="sort"){
this.parseChildren(_36[_37[i]],t[0],t[1],_2f);
}else{
this.parseChildren(i,t[0],t[1],_2f);
}
this.currentTemplate=_34;
this.paramsSet[this.currentTemplate]=new Array();
}
}
}else{
if(_2d.getAttribute("name")){
var t=this.templates[_2d.getAttribute("name")];
this.currentTemplate=t;
if(t){
this.withparams(_2d);
this.parseChildren(_2c,t[0],t[1],_2f);
this.currentTemplate=t;
this.paramsSet[this.currentTemplate]=new Array();
}
}else{
var _39=_2c.cloneNode(true);
var _3a=this.xmlDoc.createDocumentFragment();
var _36=_39.childNodes;
if(!_36[0]){
return;
}
for(var _3b,i=_36.length-1;i>=0;i--){
if(_36[i].nodeType==3||_36[i].nodeType==4){
continue;
}
if(!_36[i].nodeType==1){
continue;
}
var n=_36[i];
for(_3b in this.templates){
if(_3b=="/"){
continue;
}
var t=this.templates[_3b];
var _3d=n.selectNodes("self::"+_3b);
for(var j=_3d.length-1;j>=0;j--){
var s=_3d[j],p=s.parentNode;
this.parseChildren(s,t[0],t[1],_3a);
this.paramsSet[this.currentTemplate]=new Array();
if(_3a.childNodes){
for(var k=_3a.childNodes.length-1;k>=0;k--){
p.insertBefore(_3a.childNodes[k],s);
}
}
p.removeChild(s);
}
}
if(n.parentNode){
var p=n.parentNode;
this.p["apply-templates"].call(this,n,_2d,_2e,_3a);
if(_3a.childNodes){
for(var k=_3a.childNodes.length-1;k>=0;k--){
p.insertBefore(_3a.childNodes[k],n);
}
}
p.removeChild(n);
}
}
for(var i=_39.childNodes.length-1;i>=0;i--){
_2f.insertBefore(_39.childNodes[i],_2f.firstChild);
}
}
}
}
},cache:{},"import":function(_42,_43,_44,_45){
var _46=_43.getAttribute("href");
if(!this.cache[_46]){
var _47=new HTTP().get(_46,false,true);
this.cache[_46]=_47;
}
},"include":function(_48,_49,_4a,_4b){
},"element":function(_4c,_4d,_4e,_4f){
var _50=this.xmlDoc.createElement(_4d.getAttribute("name"));
this.parseChildren(_4c,_4d,_4e,_50);
_4f.appendChild(_50);
},"param":function(_51,_52,_53,_54){
var _55=_52.getAttribute("name");
if(!this.paramsSet["params"][_55]&&_52.firstChild){
var aux=getNodeValue(_52);
if(aux=="undefined"){
aux=false;
}
this.paramsSet["params"][_55]=aux;
}
},"with-param":function(_57,_58,_59,_5a){
},"variable":function(_5b,_5c,_5d,_5e){
var _5f=_5c.getAttribute("name");
var _60="";
if(!this.paramsSet[this.currentTemplate]){
this.paramsSet[this.currentTemplate]=new Array();
}
if(!this.paramsSet[this.currentTemplate][_5f]){
var _61=this.xmlDoc.createDocumentFragment();
this.parseChildren(_5b,_5c,_5d,_61);
var _62=_61.childNodes;
var _63=_5c.getAttribute("select");
if(_63){
_63=this.lookforVariable(_63);
try{
var _64=_5b.selectNodes(_63)[0];
if(!_64){
_60="";
}else{
if(_64.nodeType==1){
_60=_64.firstChild?_64.firstChild.nodeValue:"";
}else{
_60=typeof _64=="object"?_64.nodeValue:_64;
}
}
if(typeof _64=="number"){
_60=""+_64;
}
}
catch(e){
}
}else{
for(var i=0;i<_62.length;i++){
var _66=getNodeValue(_62[i]);
if(_66!="undefined"){
_60+=_66;
}
}
}
_60=_60.replace(/\s/g,"");
this.paramsSet[this.currentTemplate][_5f]=_60;
}
},"when":function(){
},"otherwise":function(){
},"sort":function(){
},"copy-clone":function(_67,_68,_69,_6a){
_6a=_6a.appendChild(!IS_IE?_6a.ownerDocument.importNode(_68,false):_68.cloneNode(false));
if(_6a.nodeType==1){
for(var i=0;i<_6a.attributes.length;i++){
var _6c=_6a.attributes[i].nodeValue;
if(!IS_SAFARI&&_6a.attributes[i].nodeName.match(/^xmlns/)){
continue;
}
_6a.attributes[i].nodeValue=this.variable(_6a.attributes[i].nodeValue);
_6a.attributes[i].nodeValue=_6a.attributes[i].nodeValue.replace(/\{([^\}]+)\}/g,function(m,_6e){
var _6f=XPath.selectNodes(_6e,_67)[0];
if(!_6f){
value="";
}else{
if(_6f.nodeType==1){
value=_6f.firstChild?_6f.firstChild.nodeValue:"";
}else{
value=typeof _6f=="object"?_6f.nodeValue:_6f;
}
}
return value;
});
_6a.attributes[i].nodeValue;
}
}
this.parseChildren(_67,_68,_69,_6a);
}};
this.xslSort=function(_70,_71,_72){
var _73=_70.getAttribute("select");
var _72=new Array;
var _74=new Array;
for(var i=0;i<_71.length;i++){
var _76=_71[i].selectNodes(_73);
_72[i]=_76;
_74[_76]=i;
}
_72.sort();
var _77=new Array;
for(var i=0;i<_72.length;i++){
_77[_74[_72[i]]]=i;
}
_72=_77;
};
this.parseChildren=function(_78,_79,_7a,_7b){
if(!_7a){
alert("stack empty");
return;
}
if(_7a.length==0){
return;
}
for(var i=0;i<_7a.length;i++){
_7a[i][0].call(this,_78,_7a[i][1],_7a[i][2],_7b);
}
};
this.compile=function(_7d){
var _7e=_7d.childNodes;
for(var _7f=[],i=0;i<_7e.length;i++){
var _80=_7e[i].nodeType;
if(_7e[i][TAGNAME]=="template"){
this.templates[_7e[i].getAttribute("match")||_7e[i].getAttribute("name")]=[_7e[i],this.compile(_7e[i])];
}else{
if(_7e[i][TAGNAME]=="stylesheet"){
_7f=this.compile(_7e[i]);
}else{
if(_7e[i].prefix=="xsl"){
var _81=this.p[_7e[i][TAGNAME]];
if(!_81){
alert("xsl:"+_7e[i][TAGNAME]+" is not supported at this time on this platform");
}else{
_7f.push([_81,_7e[i],this.compile(_7e[i])]);
}
}else{
if(_80!=8){
_7f.push([this.p["copy-clone"],_7e[i],this.compile(_7e[i])]);
}
}
}
}
}
return _7f;
};
this.variable=function(_82){
if(_82.indexOf("$")!=-1){
var _83=_82.match(/\$(\w)*/g);
for(var i=0;i<_83.length;i++){
var aux=_83[i].substring(1);
for(p in this.paramsSet){
if(this.paramsSet[p][aux]){
_82=_82.replace("{"+_83[i]+"}",this.paramsSet[p][aux]);
}
}
}
}
return _82;
};
this.withparams=function(_86){
var _87=_86.childNodes;
for(var i=0;i<_87.length;i++){
if(_87[i][TAGNAME]=="with-param"){
var _89=xslNode.getAttribute("name");
var _8a=_87[i].childNodes;
var _8b=xslNode.getAttribute("select");
if(_8b){
_8b=this.lookforVariable(_8b);
try{
var _8c=context.selectNodes(_8b)[0];
if(!_8c){
tempValue="";
}else{
if(_8c.nodeType==1){
tempValue=_8c.firstChild?_8c.firstChild.nodeValue:"";
}else{
tempValue=typeof _8c=="object"?_8c.nodeValue:_8c;
}
}
if(typeof _8c=="number"){
tempValue=""+_8c;
}
}
catch(e){
}
}else{
for(var i=0;i<_87.length;i++){
var _8d=getNodeValue(_87[i]);
if(_8d!="undefined"){
tempValue+=_8d;
}
}
tempValue=tempValue.replace(/\s/g,"");
this.paramsSet[this.currentTemplate][_89]=tempValue;
}
}
}
};
this.lookforVariable=function(_8e){
if(_8e.indexOf("$")!=-1){
var _8f=0;
var _90=_8e.match(/(=)?\$(\w*)/);
while(_90){
var aux=RegExp.$2;
for(p in this.paramsSet){
if(this.paramsSet[p][aux]){
_8f=1;
if(RegExp.$1=="="){
_8e=_8e.replace("$"+aux,"'"+this.paramsSet[p][aux]+"'");
}else{
var _92=this.paramsSet;
_8e=_8e.replace(/\$(\w*)/,function(d,_94){
if(_94==aux){
return _92[p][aux];
}else{
return _94;
}
});
}
}
}
if(_8f==0){
_8e=_8e.replace("$"+aux,false);
}
_90=_8e.match(/(=)?\$(\w*)/);
}
}
return _8e;
};
this.lookforTemplate=function(_95){
var _96=":"+_95;
for(look in this.templates){
if(look.match(new RegExp(_96))){
if(RegExp.rightContext==null){
return look;
}
}
}
return 0;
};
this.lookforTemplate2=function(_97){
if(_97=="/"){
return "/";
}
var _98=_97.replace(/\[.*\]/g,"");
_98=_98.split("/");
_98=_98[_98.length-1];
for(look in this.templates){
if(look.match(new RegExp(_98))){
if(RegExp.rightContext==null){
return look;
}
}
}
return 0;
};
this.importStylesheet=function(_99){
this.xslDoc=_99.nodeType==9?_99.documentElement:_99;
this.xslStack=this.compile(_99);
this.paramsSet=new Array();
this.paramsSet["params"]=new Array();
this.xslStack.push([this.p["apply-templates"],null]);
};
this.setParameter=function(_9a,_9b,_9c){
_9c=_9c?_9c:"";
this.paramsSet["params"][_9b]=_9c;
};
this.getParameter=function(_9d,_9e){
_9d=""+_9d;
if(this.paramsSet["params"][_9e]){
return this.paramsSet[_9e];
}else{
return null;
}
};
this.clearParameters=function(){
this.paramsSet=new Array();
this.paramsSet["params"]=new Array();
};
this.transformToFragment=function(doc,_a0){
this.xmlDoc=_a0.nodeType!=9?_a0.ownerDocument:_a0;
var _a1=this.xmlDoc.createDocumentFragment();
this.paramsSet["global"]=new Array();
this.currentTemplate="global";
var _a2=this.parseChildren(doc,this.xslDoc,this.xslStack,_a1);
return _a1;
};
}

Sarissa.updateCursor=function(_1,_2){
if(_1&&_1.style&&_1.style.cursor!=undefined){
_1.style.cursor=_2;
}
};
Sarissa.updateContentFromURI=function(_3,_4,_5,_6,_7){
try{
Sarissa.updateCursor(_4,"wait");
var _8=new XMLHttpRequest();
_8.open("GET",_3);
function sarissa_dhtml_loadHandler(){
if(_8.readyState==4){
Sarissa.updateContentFromNode(_8.responseXML,_4,_5,_6);
}
}
_8.onreadystatechange=sarissa_dhtml_loadHandler;
if(_7){
var _9="Sat, 1 Jan 2000 00:00:00 GMT";
_8.setRequestHeader("If-Modified-Since",_9);
}
_8.send("");
}
catch(e){
Sarissa.updateCursor(_4,"auto");
throw e;
}
};
Sarissa.updateContentFromNode=function(_a,_b,_c,_d){
try{
Sarissa.updateCursor(_b,"wait");
Sarissa.clearChildNodes(_b);
var _e=_a.nodeType==Node.DOCUMENT_NODE?_a:_a.ownerDocument;
if(_e.parseError&&_e.parseError!=0){
var _f=document.createElement("pre");
_f.appendChild(document.createTextNode(Sarissa.getParseErrorText(_e)));
_b.appendChild(_f);
}else{
if(_c){
_a=_c.transformToDocument(_a);
}
if(_b.tagName.toLowerCase()=="textarea"||_b.tagName.toLowerCase()=="input"){
_b.value=new XMLSerializer().serializeToString(_a);
}else{
if(_a.nodeType==Node.DOCUMENT_NODE||_a.ownerDocument.documentElement==_a){
_b.innerHTML=new XMLSerializer().serializeToString(_a);
}else{
_b.appendChild(_b.ownerDocument.importNode(_a,true));
}
}
}
if(_d){
_d(_a,_b);
}
}
catch(e){
throw e;
}
finally{
Sarissa.updateCursor(_b,"auto");
}
};

if(_SARISSA_HAS_DOM_FEATURE&&document.implementation.hasFeature("XPath","3.0")){
function SarissaNodeList(i){
this.length=i;
}
SarissaNodeList.prototype=new Array(0);
SarissaNodeList.prototype.constructor=Array;
SarissaNodeList.prototype.item=function(i){
return (i<0||i>=this.length)?null:this[i];
};
SarissaNodeList.prototype.expr="";
if(window.XMLDocument&&(!XMLDocument.prototype.setProperty)){
XMLDocument.prototype.setProperty=function(x,y){
};
}
Sarissa.setXpathNamespaces=function(_5,_6){
_5._sarissa_useCustomResolver=true;
var _7=_6.indexOf(" ")>-1?_6.split(" "):new Array(_6);
_5._sarissa_xpathNamespaces=new Array(_7.length);
for(var i=0;i<_7.length;i++){
var ns=_7[i];
var _a=ns.indexOf(":");
var _b=ns.indexOf("=");
if(_a>0&&_b>_a+1){
var _c=ns.substring(_a+1,_b);
var _d=ns.substring(_b+2,ns.length-1);
_5._sarissa_xpathNamespaces[_c]=_d;
}else{
throw "Bad format on namespace declaration(s) given";
}
}
};
XMLDocument.prototype._sarissa_useCustomResolver=false;
XMLDocument.prototype._sarissa_xpathNamespaces=new Array();
XMLDocument.prototype.selectNodes=function(_e,_f,_10){
var _11=this;
var _12=this._sarissa_useCustomResolver?function(_13){
var s=_11._sarissa_xpathNamespaces[_13];
if(s){
return s;
}else{
throw "No namespace URI found for prefix: '"+_13+"'";
}
}:this.createNSResolver(this.documentElement);
var _15=null;
if(!_10){
var _16=this.evaluate(_e,(_f?_f:this),_12,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,null);
var _17=new SarissaNodeList(_16.snapshotLength);
_17.expr=_e;
for(var i=0;i<_17.length;i++){
_17[i]=_16.snapshotItem(i);
}
_15=_17;
}else{
_15=_16=this.evaluate(_e,(_f?_f:this),_12,XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue;
}
return _15;
};
Element.prototype.selectNodes=function(_19){
var doc=this.ownerDocument;
if(doc.selectNodes){
return doc.selectNodes(_19,this);
}else{
throw "Method selectNodes is only supported by XML Elements";
}
};
XMLDocument.prototype.selectSingleNode=function(_1b,_1c){
var ctx=_1c?_1c:null;
return this.selectNodes(_1b,ctx,true);
};
Element.prototype.selectSingleNode=function(_1e){
var doc=this.ownerDocument;
if(doc.selectSingleNode){
return doc.selectSingleNode(_1e,this);
}else{
throw "Method selectNodes is only supported by XML Elements";
}
};
Sarissa.IS_ENABLED_SELECT_NODES=true;
}

Proj4js={defaultDatum:"WGS84",proxyScript:null,defsLookupService:"http://spatialreference.org/ref",libPath:"../lib/",transform:function(_1,_2,_3){
if(!_1.readyToUse||!_2.readyToUse){
this.reportError("Proj4js initialization for "+_1.srsCode+" not yet complete");
return;
}
if(_3.transformed){
this.log("point already transformed");
return;
}
if((_1.srsProjNumber=="900913"&&_2.datumCode!="WGS84")||(_2.srsProjNumber=="900913"&&_1.datumCode!="WGS84")){
var _4=Proj4js.WGS84;
this.transform(_1,_4,_3);
_3.transformed=false;
_1=_4;
}
if(_1.projName=="longlat"){
_3.x*=Proj4js.common.D2R;
_3.y*=Proj4js.common.D2R;
}else{
if(_1.to_meter){
_3.x*=_1.to_meter;
_3.y*=_1.to_meter;
}
_1.inverse(_3);
}
if(_1.from_greenwich){
_3.x+=_1.from_greenwich;
}
_3=this.datum_transform(_1.datum,_2.datum,_3);
if(_2.from_greenwich){
_3.x-=_2.from_greenwich;
}
if(_2.projName=="longlat"){
_3.x*=Proj4js.common.R2D;
_3.y*=Proj4js.common.R2D;
}else{
_2.forward(_3);
if(_2.to_meter){
_3.x/=_2.to_meter;
_3.y/=_2.to_meter;
}
}
_3.transformed=true;
return _3;
},datum_transform:function(_5,_6,_7){
if(_5.compare_datums(_6)){
return _7;
}
if(_5.datum_type==Proj4js.common.PJD_NODATUM||_6.datum_type==Proj4js.common.PJD_NODATUM){
return _7;
}
if(_5.datum_type==Proj4js.common.PJD_GRIDSHIFT){
alert("ERROR: Grid shift transformations are not implemented yet.");
}
if(_6.datum_type==Proj4js.common.PJD_GRIDSHIFT){
alert("ERROR: Grid shift transformations are not implemented yet.");
}
if(_5.es!=_6.es||_5.a!=_6.a||_5.datum_type==Proj4js.common.PJD_3PARAM||_5.datum_type==Proj4js.common.PJD_7PARAM||_6.datum_type==Proj4js.common.PJD_3PARAM||_6.datum_type==Proj4js.common.PJD_7PARAM){
_5.geodetic_to_geocentric(_7);
if(_5.datum_type==Proj4js.common.PJD_3PARAM||_5.datum_type==Proj4js.common.PJD_7PARAM){
_5.geocentric_to_wgs84(_7);
}
if(_6.datum_type==Proj4js.common.PJD_3PARAM||_6.datum_type==Proj4js.common.PJD_7PARAM){
_6.geocentric_from_wgs84(_7);
}
_6.geocentric_to_geodetic(_7);
}
if(_6.datum_type==Proj4js.common.PJD_GRIDSHIFT){
alert("ERROR: Grid shift transformations are not implemented yet.");
}
return _7;
},reportError:function(_8){
},log:function(_9){
},loadProjDefinition:function(_a){
if(this.defs[_a.srsCode]){
return this.defs[_a.srsCode];
}
var _b={method:"get",asynchronous:false,onSuccess:this.defsLoadedFromDisk.bind(this,_a.srsCode)};
var _c=this.libPath+"defs/"+_a.srsAuth.toUpperCase()+_a.srsProjNumber+".js";
new OpenLayers.Ajax.Request(_c,_b);
if(this.defs[_a.srsCode]){
return this.defs[_a.srsCode];
}
if(this.proxyScript){
var _c=this.proxyScript+this.defsLookupService+"/"+_a.srsAuth+"/"+_a.srsProjNumber+"/proj4";
_b.onSuccess=this.defsLoadedFromService.bind(this,_a.srsCode);
_b.onFailure=this.defsFailed.bind(this,_a.srsCode);
new OpenLayers.Ajax.Request(_c,_b);
}
return this.defs[_a.srsCode];
},defsLoadedFromDisk:function(_d,_e){
eval(_e.responseText);
},defsLoadedFromService:function(_f,_10){
this.defs[_f]=_10.responseText;
Proj4js.defs[_f]=_10.responseText;
},defsFailed:function(_11){
this.reportError("failed to load projection definition for: "+_11);
OpenLayers.Util.extend(this.defs[_11],this.defs["WGS84"]);
},loadProjCode:function(_12){
if(this.Proj[_12]){
return;
}
var _13={method:"get",asynchronous:false,onSuccess:this.loadProjCodeSuccess.bind(this,_12),onFailure:this.loadProjCodeFailure.bind(this,_12)};
var url=this.libPath+"projCode/"+_12+".js";
new OpenLayers.Ajax.Request(url,_13);
},loadProjCodeSuccess:function(_15,_16){
eval(_16.responseText);
if(this.Proj[_15].dependsOn){
this.loadProjCode(this.Proj[_15].dependsOn);
}
},loadProjCodeFailure:function(_17){
Proj4js.reportError("failed to find projection file for: "+_17);
}};
Proj4js.Proj=OpenLayers.Class({readyToUse:false,title:null,projName:null,units:null,datum:null,initialize:function(_18){
this.srsCode=_18.toUpperCase();
if(this.srsCode.indexOf("EPSG")==0){
this.srsCode=this.srsCode;
this.srsAuth="epsg";
this.srsProjNumber=this.srsCode.substring(5);
}else{
this.srsAuth="";
this.srsProjNumber=this.srsCode;
}
var _19=Proj4js.loadProjDefinition(this);
if(_19){
this.parseDefs(_19);
Proj4js.loadProjCode(this.projName);
this.callInit();
}
},callInit:function(){
Proj4js.log("projection script loaded for:"+this.projName);
OpenLayers.Util.extend(this,Proj4js.Proj[this.projName]);
this.init();
this.mapXYToLonLat=this.inverse;
this.lonLatToMapXY=this.forward;
this.readyToUse=true;
},parseDefs:function(_1a){
this.defData=_1a;
var _1b,paramVal;
var _1c=this.defData.split("+");
for(var _1d=0;_1d<_1c.length;_1d++){
var _1e=_1c[_1d].split("=");
_1b=_1e[0].toLowerCase();
paramVal=_1e[1];
switch(_1b.replace(/\s/gi,"")){
case "":
break;
case "title":
this.title=paramVal;
break;
case "proj":
this.projName=paramVal.replace(/\s/gi,"");
break;
case "units":
this.units=paramVal.replace(/\s/gi,"");
break;
case "datum":
this.datumCode=paramVal.replace(/\s/gi,"");
break;
case "nadgrids":
this.nagrids=paramVal.replace(/\s/gi,"");
break;
case "ellps":
this.ellps=paramVal.replace(/\s/gi,"");
break;
case "a":
this.a=parseFloat(paramVal);
break;
case "b":
this.b=parseFloat(paramVal);
break;
case "lat_0":
this.lat0=paramVal*Proj4js.common.D2R;
break;
case "lat_1":
this.lat1=paramVal*Proj4js.common.D2R;
break;
case "lat_2":
this.lat2=paramVal*Proj4js.common.D2R;
break;
case "lat_ts":
this.lat_ts=paramVal*Proj4js.common.D2R;
break;
case "lon_0":
this.long0=paramVal*Proj4js.common.D2R;
break;
case "x_0":
this.x0=parseFloat(paramVal);
break;
case "y_0":
this.y0=parseFloat(paramVal);
break;
case "k_0":
this.k0=parseFloat(paramVal);
break;
case "k":
this.k0=parseFloat(paramVal);
break;
case "R_A":
this.R=parseFloat(paramVal);
break;
case "zone":
this.zone=parseInt(paramVal);
break;
case "south":
this.utmSouth=true;
break;
case "towgs84":
this.datum_params=paramVal.split(",");
break;
case "to_meter":
this.to_meter=parseFloat(paramVal);
break;
case "from_greenwich":
this.from_greenwich=paramVal*Proj4js.common.D2R;
break;
case "pm":
paramVal=paramVal.replace(/\s/gi,"");
this.from_greenwich=Proj4js.PrimeMeridian[paramVal]?Proj4js.PrimeMeridian[paramVal]*Proj4js.common.D2R:0;
break;
case "no_defs":
break;
default:
Proj4js.log("Unrecognized parameter: "+_1b);
}
}
this.deriveConstants();
},deriveConstants:function(){
if(this.nagrids=="@null"){
this.datumCode="none";
}
if(this.datumCode&&this.datumCode!="none"){
var _1f=Proj4js.Datum[this.datumCode];
if(_1f){
this.datum_params=_1f.towgs84.split(",");
this.ellps=_1f.ellipse;
this.datumName=_1f.datumName;
}
}
if(!this.a){
var _20=Proj4js.Ellipsoid[this.ellps]?Proj4js.Ellipsoid[this.ellps]:Proj4js.Ellipsoid["WGS84"];
OpenLayers.Util.extend(this,_20);
}
if(this.rf&&!this.b){
this.b=(1-1/this.rf)*this.a;
}
if(Math.abs(this.a-this.b)<Proj4js.common.EPSLN){
this.sphere=true;
}
this.a2=this.a*this.a;
this.b2=this.b*this.b;
this.es=(this.a2-this.b2)/this.a2;
this.e=Math.sqrt(this.es);
this.ep2=(this.a2-this.b2)/this.b2;
if(!this.k0){
this.k0=1;
}
this.datum=new Proj4js.datum(this);
}});
Proj4js.Proj.longlat={init:function(){
},forward:function(pt){
return pt;
},inverse:function(pt){
return pt;
}};
Proj4js.defs={"WGS84":"+title=long/lat:WGS84 +proj=longlat +ellps=WGS84 +datum=WGS84","EPSG:4326":"+title=long/lat:WGS84 +proj=longlat +a=6378137.0 +b=6356752.31424518 +ellps=WGS84 +datum=WGS84","EPSG:4269":"+title=long/lat:NAD83 +proj=longlat +a=6378137.0 +b=6356752.31414036 +ellps=GRS80 +datum=NAD83"};
Proj4js.common={PI:Math.PI,HALF_PI:Math.PI*0.5,TWO_PI:Math.PI*2,FORTPI:0.7853981633974483,R2D:57.2957795131,D2R:0.0174532925199,SEC_TO_RAD:0.00000484813681109536,EPSLN:1e-10,MAX_ITER:20,COS_67P5:0.3826834323650898,AD_C:1.0026,PJD_UNKNOWN:0,PJD_3PARAM:1,PJD_7PARAM:2,PJD_GRIDSHIFT:3,PJD_WGS84:4,PJD_NODATUM:5,SRS_WGS84_SEMIMAJOR:6378137,msfnz:function(_23,_24,_25){
var con=_23*_24;
return _25/(Math.sqrt(1-con*con));
},tsfnz:function(_27,phi,_29){
var con=_27*_29;
var com=0.5*_27;
con=Math.pow(((1-con)/(1+con)),com);
return (Math.tan(0.5*(this.HALF_PI-phi))/con);
},phi2z:function(_2c,ts){
var _2e=0.5*_2c;
var con,dphi;
var phi=this.HALF_PI-2*Math.atan(ts);
for(i=0;i<=15;i++){
con=_2c*Math.sin(phi);
dphi=this.HALF_PI-2*Math.atan(ts*(Math.pow(((1-con)/(1+con)),_2e)))-phi;
phi+=dphi;
if(Math.abs(dphi)<=1e-10){
return phi;
}
}
alert("phi2z has NoConvergence");
return (-9999);
},qsfnz:function(_31,_32,_33){
var con;
if(_31>1e-7){
con=_31*_32;
return ((1-_31*_31)*(_32/(1-con*con)-(0.5/_31)*Math.log((1-con)/(1+con))));
}else{
return (2*_32);
}
},asinz:function(x){
if(Math.abs(x)>1){
x=(x>1)?1:-1;
}
return Math.asin(x);
},e0fn:function(x){
return (1-0.25*x*(1+x/16*(3+1.25*x)));
},e1fn:function(x){
return (0.375*x*(1+0.25*x*(1+0.46875*x)));
},e2fn:function(x){
return (0.05859375*x*x*(1+0.75*x));
},e3fn:function(x){
return (x*x*x*(35/3072));
},mlfn:function(e0,e1,e2,e3,phi){
return (e0*phi-e1*Math.sin(2*phi)+e2*Math.sin(4*phi)-e3*Math.sin(6*phi));
},srat:function(_3f,exp){
return (Math.pow((1-_3f)/(1+_3f),exp));
},sign:function(x){
if(x<0){
return (-1);
}else{
return (1);
}
},adjust_lon:function(x){
x=(Math.abs(x)<this.PI)?x:(x-(this.sign(x)*this.TWO_PI));
return x;
}};
Proj4js.datum=OpenLayers.Class({initialize:function(_43){
this.datum_type=Proj4js.common.PJD_WGS84;
if(_43.datumCode&&_43.datumCode=="none"){
this.datum_type=Proj4js.common.PJD_NODATUM;
}
if(_43&&_43.datum_params){
for(var i=0;i<_43.datum_params.length;i++){
_43.datum_params[i]=parseFloat(_43.datum_params[i]);
}
if(_43.datum_params[0]!=0||_43.datum_params[1]!=0||_43.datum_params[2]!=0){
this.datum_type=Proj4js.common.PJD_3PARAM;
}
if(_43.datum_params.length>3){
if(_43.datum_params[3]!=0||_43.datum_params[4]!=0||_43.datum_params[5]!=0||_43.datum_params[6]!=0){
this.datum_type=Proj4js.common.PJD_7PARAM;
_43.datum_params[3]*=Proj4js.common.SEC_TO_RAD;
_43.datum_params[4]*=Proj4js.common.SEC_TO_RAD;
_43.datum_params[5]*=Proj4js.common.SEC_TO_RAD;
_43.datum_params[6]=(_43.datum_params[6]/1000000)+1;
}
}
}
if(_43){
this.a=_43.a;
this.b=_43.b;
this.es=_43.es;
this.ep2=_43.ep2;
this.datum_params=_43.datum_params;
}
},compare_datums:function(_45){
if(this.datum_type!=_45.datum_type){
return false;
}else{
if(this.a!=_45.a||Math.abs(this.es-_45.es)>5e-11){
return false;
}else{
if(this.datum_type==Proj4js.common.PJD_3PARAM){
return (this.datum_params[0]==_45.datum_params[0]&&this.datum_params[1]==_45.datum_params[1]&&this.datum_params[2]==_45.datum_params[2]);
}else{
if(this.datum_type==Proj4js.common.PJD_7PARAM){
return (this.datum_params[0]==_45.datum_params[0]&&this.datum_params[1]==_45.datum_params[1]&&this.datum_params[2]==_45.datum_params[2]&&this.datum_params[3]==_45.datum_params[3]&&this.datum_params[4]==_45.datum_params[4]&&this.datum_params[5]==_45.datum_params[5]&&this.datum_params[6]==_45.datum_params[6]);
}else{
if(this.datum_type==Proj4js.common.PJD_GRIDSHIFT){
return strcmp(pj_param(this.params,"snadgrids").s,pj_param(_45.params,"snadgrids").s)==0;
}else{
return true;
}
}
}
}
}
},geodetic_to_geocentric:function(p){
var _47=p.x;
var _48=p.y;
var _49=p.z?p.z:0;
var X;
var Y;
var Z;
var _4d=0;
var Rn;
var _4f;
var _50;
var _51;
if(_48<-Proj4js.common.HALF_PI&&_48>-1.001*Proj4js.common.HALF_PI){
_48=-Proj4js.common.HALF_PI;
}else{
if(_48>Proj4js.common.HALF_PI&&_48<1.001*Proj4js.common.HALF_PI){
_48=Proj4js.common.HALF_PI;
}else{
if((_48<-Proj4js.common.HALF_PI)||(_48>Proj4js.common.HALF_PI)){
Proj4js.reportError("geocent:lat out of range:"+_48);
return null;
}
}
}
if(_47>Proj4js.common.PI){
_47-=(2*Proj4js.common.PI);
}
_4f=Math.sin(_48);
_51=Math.cos(_48);
_50=_4f*_4f;
Rn=this.a/(Math.sqrt(1-this.es*_50));
X=(Rn+_49)*_51*Math.cos(_47);
Y=(Rn+_49)*_51*Math.sin(_47);
Z=((Rn*(1-this.es))+_49)*_4f;
p.x=X;
p.y=Y;
p.z=Z;
return _4d;
},geocentric_to_geodetic:function(p){
var _53=1e-12;
var _54=(_53*_53);
var _55=30;
var P;
var RR;
var CT;
var ST;
var RX;
var RK;
var RN;
var _5d;
var _5e;
var _5f;
var _60;
var _61;
var _62;
var _63;
var X=p.x;
var Y=p.y;
var Z=p.z?p.z:0;
var _67;
var _68;
var _69;
_62=false;
P=Math.sqrt(X*X+Y*Y);
RR=Math.sqrt(X*X+Y*Y+Z*Z);
if(P/this.a<_53){
_62=true;
_67=0;
if(RR/this.a<_53){
_68=Proj4js.common.HALF_PI;
_69=-this.b;
return;
}
}else{
_67=Math.atan2(Y,X);
}
CT=Z/RR;
ST=P/RR;
RX=1/Math.sqrt(1-this.es*(2-this.es)*ST*ST);
_5d=ST*(1-this.es)*RX;
_5e=CT*RX;
_63=0;
do{
_63++;
RN=this.a/Math.sqrt(1-this.es*_5e*_5e);
_69=P*_5d+Z*_5e-RN*(1-this.es*_5e*_5e);
RK=this.es*RN/(RN+_69);
RX=1/Math.sqrt(1-RK*(2-RK)*ST*ST);
_5f=ST*(1-RK)*RX;
_60=CT*RX;
_61=_60*_5d-_5f*_5e;
_5d=_5f;
_5e=_60;
}while(_61*_61>_54&&_63<_55);
_68=Math.atan(_60/Math.abs(_5f));
p.x=_67;
p.y=_68;
p.z=_69;
return p;
},geocentric_to_geodetic_noniter:function(p){
var X=p.x;
var Y=p.y;
var Z=p.z?p.z:0;
var _6e;
var _6f;
var _70;
var W;
var W2;
var T0;
var T1;
var S0;
var S1;
var _77;
var _78;
var _79;
var _7a;
var _7b;
var Rn;
var Sum;
var _7e;
X=parseFloat(X);
Y=parseFloat(Y);
Z=parseFloat(Z);
_7e=false;
if(X!=0){
_6e=Math.atan2(Y,X);
}else{
if(Y>0){
_6e=Proj4js.common.HALF_PI;
}else{
if(Y<0){
_6e=-Proj4js.common.HALF_PI;
}else{
_7e=true;
_6e=0;
if(Z>0){
_6f=Proj4js.common.HALF_PI;
}else{
if(Z<0){
_6f=-Proj4js.common.HALF_PI;
}else{
_6f=Proj4js.common.HALF_PI;
_70=-this.b;
return;
}
}
}
}
}
W2=X*X+Y*Y;
W=Math.sqrt(W2);
T0=Z*Proj4js.common.AD_C;
S0=Math.sqrt(T0*T0+W2);
_77=T0/S0;
_79=W/S0;
_78=_77*_77*_77;
T1=Z+this.b*this.ep2*_78;
Sum=W-this.a*this.es*_79*_79*_79;
S1=Math.sqrt(T1*T1+Sum*Sum);
_7a=T1/S1;
_7b=Sum/S1;
Rn=this.a/Math.sqrt(1-this.es*_7a*_7a);
if(_7b>=Proj4js.common.COS_67P5){
_70=W/_7b-Rn;
}else{
if(_7b<=-Proj4js.common.COS_67P5){
_70=W/-_7b-Rn;
}else{
_70=Z/_7a+Rn*(this.es-1);
}
}
if(_7e==false){
_6f=Math.atan(_7a/_7b);
}
p.x=_6e;
p.y=_6f;
p.z=_70;
return p;
},geocentric_to_wgs84:function(p){
if(this.datum_type==Proj4js.common.PJD_3PARAM){
p.x+=this.datum_params[0];
p.y+=this.datum_params[1];
p.z+=this.datum_params[2];
}else{
var _80=this.datum_params[0];
var _81=this.datum_params[1];
var _82=this.datum_params[2];
var _83=this.datum_params[3];
var _84=this.datum_params[4];
var _85=this.datum_params[5];
var _86=this.datum_params[6];
var _87=_86*(p.x-_85*p.y+_84*p.z)+_80;
var _88=_86*(_85*p.x+p.y-_83*p.z)+_81;
var _89=_86*(-_84*p.x+_83*p.y+p.z)+_82;
p.x=_87;
p.y=_88;
p.z=_89;
}
},geocentric_from_wgs84:function(p){
if(this.datum_type==Proj4js.common.PJD_3PARAM){
p.x-=this.datum_params[0];
p.y-=this.datum_params[1];
p.z-=this.datum_params[2];
}else{
var _8b=this.datum_params[0];
var _8c=this.datum_params[1];
var _8d=this.datum_params[2];
var _8e=this.datum_params[3];
var _8f=this.datum_params[4];
var _90=this.datum_params[5];
var _91=this.datum_params[6];
var _92=(p.x-_8b)/_91;
var _93=(p.y-_8c)/_91;
var _94=(p.z-_8d)/_91;
p.x=_92+_90*_93-_8f*_94;
p.y=-_90*_92+_93+_8e*_94;
p.z=_8f*_92-_8e*_93+_94;
}
}});
Proj4js.Point=OpenLayers.Class({initialize:function(x,y,z){
if(typeof x=="object"){
this.x=x[0];
this.y=x[1];
this.z=x[2]||0;
}else{
this.x=x;
this.y=y;
this.z=z||0;
}
},clone:function(){
return new Proj4js.Point(this.x,this.y,this.z);
},toString:function(){
return ("x="+this.x+",y="+this.y);
},toShortString:function(){
return (this.x+", "+this.y);
}});
Proj4js.PrimeMeridian={"greenwich":0,"lisbon":-9.131906111111,"paris":2.337229166667,"bogota":-74.080916666667,"madrid":-3.687938888889,"rome":12.452333333333,"bern":7.439583333333,"jakarta":106.807719444444,"ferro":-17.666666666667,"brussels":4.367975,"stockholm":18.058277777778,"athens":23.7163375,"oslo":10.722916666667};
Proj4js.Ellipsoid={"MERIT":{a:6378137,rf:298.257,ellipseName:"MERIT 1983"},"SGS85":{a:6378136,rf:298.257,ellipseName:"Soviet Geodetic System 85"},"GRS80":{a:6378137,rf:298.257222101,ellipseName:"GRS 1980(IUGG, 1980)"},"IAU76":{a:6378140,rf:298.257,ellipseName:"IAU 1976"},"airy":{a:6377563.396,b:6356256.91,ellipseName:"Airy 1830"},"APL4.":{a:6378137,rf:298.25,ellipseName:"Appl. Physics. 1965"},"NWL9D":{a:6378145,rf:298.25,ellipseName:"Naval Weapons Lab., 1965"},"mod_airy":{a:6377340.189,b:6356034.446,ellipseName:"Modified Airy"},"andrae":{a:6377104.43,rf:300,ellipseName:"Andrae 1876 (Den., Iclnd.)"},"aust_SA":{a:6378160,rf:298.25,ellipseName:"Australian Natl & S. Amer. 1969"},"GRS67":{a:6378160,rf:298.247167427,ellipseName:"GRS 67(IUGG 1967)"},"bessel":{a:6377397.155,rf:299.1528128,ellipseName:"Bessel 1841"},"bess_nam":{a:6377483.865,rf:299.1528128,ellipseName:"Bessel 1841 (Namibia)"},"clrk66":{a:6378206.4,b:6356583.8,ellipseName:"Clarke 1866"},"clrk80":{a:6378249.145,rf:293.4663,ellipseName:"Clarke 1880 mod."},"CPM":{a:6375738.7,rf:334.29,ellipseName:"Comm. des Poids et Mesures 1799"},"delmbr":{a:6376428,rf:311.5,ellipseName:"Delambre 1810 (Belgium)"},"engelis":{a:6378136.05,rf:298.2566,ellipseName:"Engelis 1985"},"evrst30":{a:6377276.345,rf:300.8017,ellipseName:"Everest 1830"},"evrst48":{a:6377304.063,rf:300.8017,ellipseName:"Everest 1948"},"evrst56":{a:6377301.243,rf:300.8017,ellipseName:"Everest 1956"},"evrst69":{a:6377295.664,rf:300.8017,ellipseName:"Everest 1969"},"evrstSS":{a:6377298.556,rf:300.8017,ellipseName:"Everest (Sabah & Sarawak)"},"fschr60":{a:6378166,rf:298.3,ellipseName:"Fischer (Mercury Datum) 1960"},"fschr60m":{a:6378155,rf:298.3,ellipseName:"Fischer 1960"},"fschr68":{a:6378150,rf:298.3,ellipseName:"Fischer 1968"},"helmert":{a:6378200,rf:298.3,ellipseName:"Helmert 1906"},"hough":{a:6378270,rf:297,ellipseName:"Hough"},"intl":{a:6378388,rf:297,ellipseName:"International 1909 (Hayford)"},"kaula":{a:6378163,rf:298.24,ellipseName:"Kaula 1961"},"lerch":{a:6378139,rf:298.257,ellipseName:"Lerch 1979"},"mprts":{a:6397300,rf:191,ellipseName:"Maupertius 1738"},"new_intl":{a:6378157.5,b:6356772.2,ellipseName:"New International 1967"},"plessis":{a:6376523,rf:6355863,ellipseName:"Plessis 1817 (France)"},"krass":{a:6378245,rf:298.3,ellipseName:"Krassovsky, 1942"},"SEasia":{a:6378155,b:6356773.3205,ellipseName:"Southeast Asia"},"walbeck":{a:6376896,b:6355834.8467,ellipseName:"Walbeck"},"WGS60":{a:6378165,rf:298.3,ellipseName:"WGS 60"},"WGS66":{a:6378145,rf:298.25,ellipseName:"WGS 66"},"WGS72":{a:6378135,rf:298.26,ellipseName:"WGS 72"},"WGS84":{a:6378137,rf:298.257223563,ellipseName:"WGS 84"},"sphere":{a:6370997,b:6370997,ellipseName:"Normal Sphere (r=6370997)"}};
Proj4js.Datum={"WGS84":{towgs84:"0,0,0",ellipse:"WGS84",datumName:""},"GGRS87":{towgs84:"-199.87,74.79,246.62",ellipse:"GRS80",datumName:"Greek_Geodetic_Reference_System_1987"},"NAD83":{towgs84:"0,0,0",ellipse:"GRS80",datumName:"North_American_Datum_1983"},"NAD27":{nadgrids:"@conus,@alaska,@ntv2_0.gsb,@ntv1_can.dat",ellipse:"clrk66",datumName:"North_American_Datum_1927"},"potsdam":{towgs84:"606.0,23.0,413.0",ellipse:"bessel",datumName:"Potsdam Rauenberg 1950 DHDN"},"carthage":{towgs84:"-263.0,6.0,431.0",ellipse:"clark80",datumName:"Carthage 1934 Tunisia"},"hermannskogel":{towgs84:"653.0,-212.0,449.0",ellipse:"bessel",datumName:"Hermannskogel"},"ire65":{towgs84:"482.530,-130.596,564.557,-1.042,-0.214,-0.631,8.15",ellipse:"mod_airy",datumName:"Ireland 1965"},"nzgd49":{towgs84:"59.47,-5.04,187.44,0.47,-0.1,1.024,-4.5993",ellipse:"intl",datumName:"New Zealand Geodetic Datum 1949"},"OSGB36":{towgs84:"446.448,-125.157,542.060,0.1502,0.2470,0.8421,-20.4894",ellipse:"airy",datumName:"Airy 1830"}};
Proj4js.WGS84=new Proj4js.Proj("WGS84");
Proj4js.Datum["OSB36"]=Proj4js.Datum["OSGB36"];
Proj4js.Proj.sterea={dependsOn:"gauss",init:function(){
Proj4js.Proj["gauss"].init.apply(this);
if(!this.rc){
Proj4js.reportError("sterea:init:E_ERROR_0");
return;
}
this.sinc0=Math.sin(this.phic0);
this.cosc0=Math.cos(this.phic0);
this.R2=2*this.rc;
if(!this.title){
this.title="Oblique Stereographic Alternative";
}
},forward:function(p){
p.x=Proj4js.common.adjust_lon(p.x-this.long0);
Proj4js.Proj["gauss"].forward.apply(this,[p]);
sinc=Math.sin(p.y);
cosc=Math.cos(p.y);
cosl=Math.cos(p.x);
k=this.k0*this.R2/(1+this.sinc0*sinc+this.cosc0*cosc*cosl);
p.x=k*cosc*Math.sin(p.x);
p.y=k*(this.cosc0*sinc-this.sinc0*cosc*cosl);
p.x=this.a*p.x+this.x0;
p.y=this.a*p.y+this.y0;
return p;
},inverse:function(p){
var lon,lat;
p.x=(p.x-this.x0)/this.a;
p.y=(p.y-this.y0)/this.a;
p.x/=this.k0;
p.y/=this.k0;
if((rho=Math.sqrt(p.x*p.x+p.y*p.y))){
c=2*Math.atan2(rho,this.R2);
sinc=Math.sin(c);
cosc=Math.cos(c);
lat=Math.asin(cosc*this.sinc0+p.y*sinc*this.cosc0/rho);
lon=Math.atan2(p.x*sinc,rho*this.cosc0*cosc-p.y*this.sinc0*sinc);
}else{
lat=this.phic0;
lon=0;
}
p.x=lon;
p.y=lat;
Proj4js.Proj["gauss"].inverse.apply(this,[p]);
p.x=Proj4js.common.adjust_lon(p.x+this.long0);
return p;
}};
Proj4js.Proj.aea={init:function(){
if(Math.abs(this.lat1+this.lat2)<Proj4js.common.EPSLN){
Proj4js.reportError("aeaInitEqualLatitudes");
return;
}
this.temp=this.b/this.a;
this.es=1-Math.pow(this.temp,2);
this.e3=Math.sqrt(this.es);
this.sin_po=Math.sin(this.lat1);
this.cos_po=Math.cos(this.lat1);
this.t1=this.sin_po;
this.con=this.sin_po;
this.ms1=Proj4js.common.msfnz(this.e3,this.sin_po,this.cos_po);
this.qs1=Proj4js.common.qsfnz(this.e3,this.sin_po,this.cos_po);
this.sin_po=Math.sin(this.lat2);
this.cos_po=Math.cos(this.lat2);
this.t2=this.sin_po;
this.ms2=Proj4js.common.msfnz(this.e3,this.sin_po,this.cos_po);
this.qs2=Proj4js.common.qsfnz(this.e3,this.sin_po,this.cos_po);
this.sin_po=Math.sin(this.lat0);
this.cos_po=Math.cos(this.lat0);
this.t3=this.sin_po;
this.qs0=Proj4js.common.qsfnz(this.e3,this.sin_po,this.cos_po);
if(Math.abs(this.lat1-this.lat2)>Proj4js.common.EPSLN){
this.ns0=(this.ms1*this.ms1-this.ms2*this.ms2)/(this.qs2-this.qs1);
}else{
this.ns0=this.con;
}
this.c=this.ms1*this.ms1+this.ns0*this.qs1;
this.rh=this.a*Math.sqrt(this.c-this.ns0*this.qs0)/this.ns0;
},forward:function(p){
var lon=p.x;
var lat=p.y;
this.sin_phi=Math.sin(lat);
this.cos_phi=Math.cos(lat);
var qs=Proj4js.common.qsfnz(this.e3,this.sin_phi,this.cos_phi);
var rh1=this.a*Math.sqrt(this.c-this.ns0*qs)/this.ns0;
var _a0=this.ns0*Proj4js.common.adjust_lon(lon-this.long0);
var x=rh1*Math.sin(_a0)+this.x0;
var y=this.rh-rh1*Math.cos(_a0)+this.y0;
p.x=x;
p.y=y;
return p;
},inverse:function(p){
var rh1,qs,con,theta,lon,lat;
p.x-=this.x0;
p.y=this.rh-p.y+this.y0;
if(this.ns0>=0){
rh1=Math.sqrt(p.x*p.x+p.y*p.y);
con=1;
}else{
rh1=-Math.sqrt(p.x*p.x+p.y*p.y);
con=-1;
}
theta=0;
if(rh1!=0){
theta=Math.atan2(con*p.x,con*p.y);
}
con=rh1*this.ns0/this.a;
qs=(this.c-con*con)/this.ns0;
if(this.e3>=1e-10){
con=1-0.5*(1-this.es)*Math.log((1-this.e3)/(1+this.e3))/this.e3;
if(Math.abs(Math.abs(con)-Math.abs(qs))>1e-10){
lat=this.phi1z(this.e3,qs);
}else{
if(qs>=0){
lat=0.5*PI;
}else{
lat=-0.5*PI;
}
}
}else{
lat=this.phi1z(e3,qs);
}
lon=Proj4js.common.adjust_lon(theta/this.ns0+this.long0);
p.x=lon;
p.y=lat;
return p;
},phi1z:function(_a5,qs){
var con,com,dphi;
var phi=Proj4js.common.asinz(0.5*qs);
if(_a5<Proj4js.common.EPSLN){
return phi;
}
var _a9=_a5*_a5;
for(var i=1;i<=25;i++){
sinphi=Math.sin(phi);
cosphi=Math.cos(phi);
con=_a5*sinphi;
com=1-con*con;
dphi=0.5*com*com/cosphi*(qs/(1-_a9)-sinphi/com+0.5/_a5*Math.log((1-con)/(1+con)));
phi=phi+dphi;
if(Math.abs(dphi)<=1e-7){
return phi;
}
}
Proj4js.reportError("aea:phi1z:Convergence error");
return null;
}};
function phi4z(_ab,e0,e1,e2,e3,a,b,c,phi){
var _b4,sin2ph,tanph,ml,mlp,con1,con2,con3,dphi,i;
phi=a;
for(i=1;i<=15;i++){
_b4=Math.sin(phi);
tanphi=Math.tan(phi);
c=tanphi*Math.sqrt(1-_ab*_b4*_b4);
sin2ph=Math.sin(2*phi);
ml=e0*phi-e1*sin2ph+e2*Math.sin(4*phi)-e3*Math.sin(6*phi);
mlp=e0-2*e1*Math.cos(2*phi)+4*e2*Math.cos(4*phi)-6*e3*Math.cos(6*phi);
con1=2*ml+c*(ml*ml+b)-2*a*(c*ml+1);
con2=_ab*sin2ph*(ml*ml+b-2*a*ml)/(2*c);
con3=2*(a-ml)*(c*mlp-2/sin2ph)-2*mlp;
dphi=con1/(con2+con3);
phi+=dphi;
if(Math.abs(dphi)<=1e-10){
return (phi);
}
}
Proj4js.reportError("phi4z: No convergence");
return null;
}
function e4fn(x){
var con,com;
con=1+x;
com=1-x;
return (Math.sqrt((Math.pow(con,con))*(Math.pow(com,com))));
}
Proj4js.Proj.poly={init:function(){
var _b7;
if(this.lat0=0){
this.lat0=90;
}
this.temp=this.b/this.a;
this.es=1-Math.pow(this.temp,2);
this.e=Math.sqrt(this.es);
this.e0=Proj4js.common.e0fn(this.es);
this.e1=Proj4js.common.e1fn(this.es);
this.e2=Proj4js.common.e2fn(this.es);
this.e3=Proj4js.common.e3fn(this.es);
this.ml0=Proj4js.common.mlfn(this.e0,this.e1,this.e2,this.e3,this.lat0);
},forward:function(p){
var _b9,cosphi;
var al;
var c;
var con,ml;
var ms;
var x,y;
var lon=p.x;
var lat=p.y;
con=Proj4js.common.adjust_lon(lon-this.long0);
if(Math.abs(lat)<=1e-7){
x=this.x0+this.a*con;
y=this.y0-this.a*this.ml0;
}else{
_b9=Math.sin(lat);
cosphi=Math.cos(lat);
ml=Proj4js.common.mlfn(this.e0,this.e1,this.e2,this.e3,lat);
ms=Proj4js.common.msfnz(this.e,_b9,cosphi);
con=_b9;
x=this.x0+this.a*ms*Math.sin(con)/_b9;
y=this.y0+this.a*(ml-this.ml0+ms*(1-Math.cos(con))/_b9);
}
p.x=x;
p.y=y;
return p;
},inverse:function(p){
var _c2,cos_phi;
var al;
var b;
var c;
var con,ml;
var _c7;
var lon,lat;
p.x-=this.x0;
p.y-=this.y0;
al=this.ml0+p.y/this.a;
_c7=0;
if(Math.abs(al)<=1e-7){
lon=p.x/this.a+this.long0;
lat=0;
}else{
b=al*al+(p.x/this.a)*(p.x/this.a);
_c7=phi4z(this.es,this.e0,this.e1,this.e2,this.e3,this.al,b,c,lat);
if(_c7!=1){
return (_c7);
}
lon=Proj4js.common.adjust_lon((asinz(p.x*c/this.a)/Math.sin(lat))+this.long0);
}
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.equi={init:function(){
if(!this.x0){
this.x0=0;
}
if(!this.y0){
this.y0=0;
}
if(!this.lat0){
this.lat0=0;
}
if(!this.long0){
this.long0=0;
}
},forward:function(p){
var lon=p.x;
var lat=p.y;
var _cc=Proj4js.common.adjust_lon(lon-this.long0);
var x=this.x0+this.a*_cc*Math.cos(this.lat0);
var y=this.y0+this.a*lat;
this.t1=x;
this.t2=Math.cos(this.lat0);
p.x=x;
p.y=y;
return p;
},inverse:function(p){
p.x-=this.x0;
p.y-=this.y0;
var lat=p.y/this.a;
if(Math.abs(lat)>Proj4js.common.HALF_PI){
Proj4js.reportError("equi:Inv:DataError");
}
var lon=Proj4js.common.adjust_lon(this.long0+p.x/(this.a*Math.cos(this.lat0)));
p.x=lon;
p.y=lat;
}};
Proj4js.Proj.merc={init:function(){
if(this.lat_ts){
if(this.sphere){
this.k0=Math.cos(this.lat_ts);
}else{
this.k0=Proj4js.common.msfnz(this.es,Math.sin(this.lat_ts),Math.cos(this.lat_ts));
}
}
},forward:function(p){
var lon=p.x;
var lat=p.y;
if(lat*Proj4js.common.R2D>90&&lat*Proj4js.common.R2D<-90&&lon*Proj4js.common.R2D>180&&lon*Proj4js.common.R2D<-180){
Proj4js.reportError("merc:forward: llInputOutOfRange: "+lon+" : "+lat);
return null;
}
var x,y;
if(Math.abs(Math.abs(lat)-Proj4js.common.HALF_PI)<=Proj4js.common.EPSLN){
Proj4js.reportError("merc:forward: ll2mAtPoles");
return null;
}else{
if(this.sphere){
x=this.x0+this.a*this.k0*Proj4js.common.adjust_lon(lon-this.long0);
y=this.y0+this.a*this.k0*Math.log(Math.tan(Proj4js.common.FORTPI+0.5*lat));
}else{
var _d6=Math.sin(lat);
var ts=Proj4js.common.tsfnz(this.e,lat,_d6);
x=this.x0+this.a*this.k0*Proj4js.common.adjust_lon(lon-this.long0);
y=this.y0-this.a*this.k0*Math.log(ts);
}
p.x=x;
p.y=y;
return p;
}
},inverse:function(p){
var x=p.x-this.x0;
var y=p.y-this.y0;
var lon,lat;
if(this.sphere){
lat=Proj4js.common.HALF_PI-2*Math.atan(Math.exp(-y/this.a*this.k0));
}else{
var ts=Math.exp(-y/(this.a*this.k0));
lat=Proj4js.common.phi2z(this.e,ts);
if(lat==-9999){
Proj4js.reportError("merc:inverse: lat = -9999");
return null;
}
}
lon=Proj4js.common.adjust_lon(this.long0+x/(this.a*this.k0));
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.utm={dependsOn:"tmerc",init:function(){
if(!this.zone){
Proj4js.reportError("utm:init: zone must be specified for UTM");
return;
}
this.lat0=0;
this.long0=((6*Math.abs(this.zone))-183)*Proj4js.common.D2R;
this.x0=500000;
this.y0=this.utmSouth?10000000:0;
this.k0=0.9996;
Proj4js.Proj["tmerc"].init.apply(this);
this.forward=Proj4js.Proj["tmerc"].forward;
this.inverse=Proj4js.Proj["tmerc"].inverse;
}};
Proj4js.Proj.eqdc={init:function(){
if(!this.mode){
this.mode=0;
}
this.temp=this.b/this.a;
this.es=1-Math.pow(this.temp,2);
this.e=Math.sqrt(this.es);
this.e0=Proj4js.common.e0fn(this.es);
this.e1=Proj4js.common.e1fn(this.es);
this.e2=Proj4js.common.e2fn(this.es);
this.e3=Proj4js.common.e3fn(this.es);
this.sinphi=Math.sin(this.lat1);
this.cosphi=Math.cos(this.lat1);
this.ms1=Proj4js.common.msfnz(this.e,this.sinphi,this.cosphi);
this.ml1=Proj4js.common.mlfn(this.e0,this.e1,this.e2,this.e3,this.lat1);
if(this.mode!=0){
if(Math.abs(this.lat1+this.lat2)<Proj4js.common.EPSLN){
Proj4js.reportError("eqdc:Init:EqualLatitudes");
}
this.sinphi=Math.sin(this.lat2);
this.cosphi=Math.cos(this.lat2);
this.ms2=Proj4js.common.msfnz(this.e,this.sinphi,this.cosphi);
this.ml2=Proj4js.common.mlfn(this.e0,this.e1,this.e2,this.e3,this.lat2);
if(Math.abs(this.lat1-this.lat2)>=Proj4js.common.EPSLN){
this.ns=(this.ms1-this.ms2)/(this.ml2-this.ml1);
}else{
this.ns=this.sinphi;
}
}else{
this.ns=this.sinphi;
}
this.g=this.ml1+this.ms1/this.ns;
this.ml0=Proj4js.common.mlfn(this.e0,this.e1,this.e2,this.e3,this.lat0);
this.rh=this.a*(this.g-this.ml0);
},forward:function(p){
var lon=p.x;
var lat=p.y;
var ml=Proj4js.common.mlfn(this.e0,this.e1,this.e2,this.e3,lat);
var rh1=this.a*(this.g-ml);
var _e2=this.ns*Proj4js.common.adjust_lon(lon-this.long0);
var x=this.x0+rh1*Math.sin(_e2);
var y=this.y0+this.rh-rh1*Math.cos(_e2);
p.x=x;
p.y=y;
return p;
},inverse:function(p){
p.x-=this.x0;
p.y=this.rh-p.y+this.y0;
var con,rh1;
if(this.ns>=0){
var rh1=Math.sqrt(p.x*p.x+p.y*p.y);
var con=1;
}else{
rh1=-Math.sqrt(p.x*p.x+p.y*p.y);
con=-1;
}
var _e8=0;
if(rh1!=0){
_e8=Math.atan2(con*p.x,con*p.y);
}
var ml=this.g-rh1/this.a;
var lat=this.phi3z(this.ml,this.e0,this.e1,this.e2,this.e3);
var lon=Proj4js.common.adjust_lon(this.long0+_e8/this.ns);
p.x=lon;
p.y=lat;
return p;
},phi3z:function(ml,e0,e1,e2,e3){
var phi;
var _f2;
phi=ml;
for(var i=0;i<15;i++){
_f2=(ml+e1*Math.sin(2*phi)-e2*Math.sin(4*phi)+e3*Math.sin(6*phi))/e0-phi;
phi+=_f2;
if(Math.abs(_f2)<=1e-10){
return phi;
}
}
Proj4js.reportError("PHI3Z-CONV:Latitude failed to converge after 15 iterations");
return null;
}};
Proj4js.Proj.tmerc={init:function(){
this.e0=Proj4js.common.e0fn(this.es);
this.e1=Proj4js.common.e1fn(this.es);
this.e2=Proj4js.common.e2fn(this.es);
this.e3=Proj4js.common.e3fn(this.es);
this.ml0=this.a*Proj4js.common.mlfn(this.e0,this.e1,this.e2,this.e3,this.lat0);
},forward:function(p){
var lon=p.x;
var lat=p.y;
var _f7=Proj4js.common.adjust_lon(lon-this.long0);
var con;
var x,y;
var _fa=Math.sin(lat);
var _fb=Math.cos(lat);
if(this.sphere){
var b=_fb*Math.sin(_f7);
if((Math.abs(Math.abs(b)-1))<1e-10){
Proj4js.reportError("tmerc:forward: Point projects into infinity");
return (93);
}else{
x=0.5*this.a*this.k0*Math.log((1+b)/(1-b));
con=Math.acos(_fb*Math.cos(_f7)/Math.sqrt(1-b*b));
if(lat<0){
con=-con;
}
y=this.a*this.k0*(con-this.lat0);
}
}else{
var al=_fb*_f7;
var als=Math.pow(al,2);
var c=this.ep2*Math.pow(_fb,2);
var tq=Math.tan(lat);
var t=Math.pow(tq,2);
con=1-this.es*Math.pow(_fa,2);
var n=this.a/Math.sqrt(con);
var ml=this.a*Proj4js.common.mlfn(this.e0,this.e1,this.e2,this.e3,lat);
x=this.k0*n*al*(1+als/6*(1-t+c+als/20*(5-18*t+Math.pow(t,2)+72*c-58*this.ep2)))+this.x0;
y=this.k0*(ml-this.ml0+n*tq*(als*(0.5+als/24*(5-t+9*c+4*Math.pow(c,2)+als/30*(61-58*t+Math.pow(t,2)+600*c-330*this.ep2)))))+this.y0;
}
p.x=x;
p.y=y;
return p;
},inverse:function(p){
var con,phi;
var _106;
var i;
var _108=6;
var lat,lon;
if(this.sphere){
var f=Math.exp(p.x/(this.a*this.k0));
var g=0.5*(f-1/f);
var temp=this.lat0+p.y/(this.a*this.k0);
var h=Math.cos(temp);
con=Math.sqrt((1-h*h)/(1+g*g));
lat=Math.asinz(con);
if(temp<0){
lat=-lat;
}
if((g==0)&&(h==0)){
lon=this.long0;
}else{
lon=Proj4js.common.adjust_lon(Math.atan2(g,h)+this.long0);
}
}else{
var x=p.x-this.x0;
var y=p.y-this.y0;
con=(this.ml0+y/this.k0)/this.a;
phi=con;
for(i=0;;i++){
_106=((con+this.e1*Math.sin(2*phi)-this.e2*Math.sin(4*phi)+this.e3*Math.sin(6*phi))/this.e0)-phi;
phi+=_106;
if(Math.abs(_106)<=Proj4js.common.EPSLN){
break;
}
if(i>=_108){
Proj4js.reportError("tmerc:inverse: Latitude failed to converge");
return (95);
}
}
if(Math.abs(phi)<Proj4js.common.HALF_PI){
var _110=Math.sin(phi);
var _111=Math.cos(phi);
var _112=Math.tan(phi);
var c=this.ep2*Math.pow(_111,2);
var cs=Math.pow(c,2);
var t=Math.pow(_112,2);
var ts=Math.pow(t,2);
con=1-this.es*Math.pow(_110,2);
var n=this.a/Math.sqrt(con);
var r=n*(1-this.es)/con;
var d=x/(n*this.k0);
var ds=Math.pow(d,2);
lat=phi-(n*_112*ds/r)*(0.5-ds/24*(5+3*t+10*c-4*cs-9*this.ep2-ds/30*(61+90*t+298*c+45*ts-252*this.ep2-3*cs)));
lon=Proj4js.common.adjust_lon(this.long0+(d*(1-ds/6*(1+2*t+c-ds/20*(5-2*c+28*t-3*cs+8*this.ep2+24*ts)))/_111));
}else{
lat=Proj4js.common.HALF_PI*Proj4js.common.sign(y);
lon=this.long0;
}
}
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.defs["GOOGLE"]="+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs";
Proj4js.defs["EPSG:900913"]=Proj4js.defs["GOOGLE"];
Proj4js.Proj.ortho={init:function(def){
this.sin_p14=Math.sin(this.lat0);
this.cos_p14=Math.cos(this.lat0);
},forward:function(p){
var _11d,cosphi;
var dlon;
var _11f;
var ksp;
var g;
var lon=p.x;
var lat=p.y;
dlon=Proj4js.common.adjust_lon(lon-this.long0);
_11d=Math.sin(lat);
cosphi=Math.cos(lat);
_11f=Math.cos(dlon);
g=this.sin_p14*_11d+this.cos_p14*cosphi*_11f;
ksp=1;
if((g>0)||(Math.abs(g)<=Proj4js.common.EPSLN)){
var x=this.a*ksp*cosphi*Math.sin(dlon);
var y=this.y0+this.a*ksp*(this.cos_p14*_11d-this.sin_p14*cosphi*_11f);
}else{
Proj4js.reportError("orthoFwdPointError");
}
p.x=x;
p.y=y;
return p;
},inverse:function(p){
var rh;
var z;
var sinz,cosz;
var temp;
var con;
var lon,lat;
p.x-=this.x0;
p.y-=this.y0;
rh=Math.sqrt(p.x*p.x+p.y*p.y);
if(rh>this.a+1e-7){
Proj4js.reportError("orthoInvDataError");
}
z=Proj4js.common.asinz(rh/this.a);
sinz=Math.sin(z);
cosi=Math.cos(z);
lon=this.long0;
if(Math.abs(rh)<=Proj4js.common.EPSLN){
lat=this.lat0;
}
lat=Proj4js.common.asinz(cosz*this.sin_p14+(y*sinz*this.cos_p14)/rh);
con=Math.abs(lat0)-Proj4js.common.HALF_PI;
if(Math.abs(con)<=Proj4js.common.EPSLN){
if(this.lat0>=0){
lon=Proj4js.common.adjust_lon(this.long0+Math.atan2(p.x,-p.y));
}else{
lon=Proj4js.common.adjust_lon(this.long0-Math.atan2(-p.x,p.y));
}
}
con=cosz-this.sin_p14*Math.sin(lat);
if((Math.abs(con)>=Proj4js.common.EPSLN)||(Math.abs(x)>=Proj4js.common.EPSLN)){
lon=Proj4js.common.adjust_lon(this.long0+Math.atan2((p.x*sinz*this.cos_p14),(con*rh)));
}
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.stere={ssfn_:function(phit,_12e,_12f){
_12e*=_12f;
return (Math.tan(0.5*(Proj4js.common.HALF_PI+phit))*Math.pow((1-_12e)/(1+_12e),0.5*_12f));
},TOL:1e-8,NITER:8,CONV:1e-10,S_POLE:0,N_POLE:1,OBLIQ:2,EQUIT:3,init:function(){
this.phits=this.lat_ts?this.lat_ts:Proj4js.common.HALF_PI;
var t=Math.abs(this.lat0);
if((Math.abs(t)-Proj4js.common.HALF_PI)<Proj4js.common.EPSLN){
this.mode=this.lat0<0?this.S_POLE:this.N_POLE;
}else{
this.mode=t>Proj4js.common.EPSLN?this.OBLIQ:this.EQUIT;
}
this.phits=Math.abs(this.phits);
if(this.es){
var X;
switch(this.mode){
case this.N_POLE:
case this.S_POLE:
if(Math.abs(this.phits-Proj4js.common.HALF_PI)<Proj4js.common.EPSLN){
this.akm1=2*this.k0/Math.sqrt(Math.pow(1+this.e,1+this.e)*Math.pow(1-this.e,1-this.e));
}else{
t=Math.sin(this.phits);
this.akm1=Math.cos(this.phits)/Proj4js.common.tsfnz(this.e,this.phits,t);
t*=this.e;
this.akm1/=Math.sqrt(1-t*t);
}
break;
case this.EQUIT:
this.akm1=2*this.k0;
break;
case this.OBLIQ:
t=Math.sin(this.lat0);
X=2*Math.atan(this.ssfn_(this.lat0,t,this.e))-Proj4js.common.HALF_PI;
t*=this.e;
this.akm1=2*this.k0*Math.cos(this.lat0)/Math.sqrt(1-t*t);
this.sinX1=Math.sin(X);
this.cosX1=Math.cos(X);
break;
}
}else{
switch(this.mode){
case this.OBLIQ:
this.sinph0=Math.sin(this.lat0);
this.cosph0=Math.cos(this.lat0);
case this.EQUIT:
this.akm1=2*this.k0;
break;
case this.S_POLE:
case this.N_POLE:
this.akm1=Math.abs(this.phits-Proj4js.common.HALF_PI)>=Proj4js.common.EPSLN?Math.cos(this.phits)/Math.tan(Proj4js.common.FORTPI-0.5*this.phits):2*this.k0;
break;
}
}
},forward:function(p){
var lon=p.x;
var lat=p.y;
var x,y;
if(this.sphere){
var _136,cosphi,coslam,sinlam;
_136=Math.sin(lat);
cosphi=Math.cos(lat);
coslam=Math.cos(lon);
sinlam=Math.sin(lon);
switch(this.mode){
case this.EQUIT:
y=1+cosphi*coslam;
if(y<=Proj4js.common.EPSLN){
F_ERROR;
}
y=this.akm1/y;
x=y*cosphi*sinlam;
y*=_136;
break;
case this.OBLIQ:
y=1+this.sinph0*_136+this.cosph0*cosphi*coslam;
if(y<=Proj4js.common.EPSLN){
F_ERROR;
}
y=this.akm1/y;
x=y*cosphi*sinlam;
y*=this.cosph0*_136-this.sinph0*cosphi*coslam;
break;
case this.N_POLE:
coslam=-coslam;
lat=-lat;
case this.S_POLE:
if(Math.abs(lat-Proj4js.common.HALF_PI)<this.TOL){
F_ERROR;
}
y=this.akm1*Math.tan(Proj4js.common.FORTPI+0.5*lat);
x=sinlam*y;
y*=coslam;
break;
}
}else{
coslam=Math.cos(lon);
sinlam=Math.sin(lon);
_136=Math.sin(lat);
if(this.mode==this.OBLIQ||this.mode==this.EQUIT){
X=2*Math.atan(this.ssfn_(lat,_136,this.e));
sinX=Math.sin(X-Proj4js.common.HALF_PI);
cosX=Math.cos(X);
}
switch(this.mode){
case this.OBLIQ:
A=this.akm1/(this.cosX1*(1+this.sinX1*sinX+this.cosX1*cosX*coslam));
y=A*(this.cosX1*sinX-this.sinX1*cosX*coslam);
x=A*cosX;
break;
case this.EQUIT:
A=2*this.akm1/(1+cosX*coslam);
y=A*sinX;
x=A*cosX;
break;
case this.S_POLE:
lat=-lat;
coslam=-coslam;
_136=-_136;
case this.N_POLE:
x=this.akm1*Proj4js.common.tsfnz(this.e,lat,_136);
y=-x*coslam;
break;
}
x=x*sinlam;
}
p.x=x*this.a+this.x0;
p.y=y*this.a+this.y0;
return p;
},inverse:function(p){
var x=(p.x-this.x0)/this.a;
var y=(p.y-this.y0)/this.a;
var lon,lat;
var _13b,sinphi,tp=0,phi_l=0,rho,halfe=0,pi2=0;
var i;
if(this.sphere){
var c,rh,sinc,cosc;
rh=Math.sqrt(x*x+y*y);
c=2*Math.atan(rh/this.akm1);
sinc=Math.sin(c);
cosc=Math.cos(c);
lon=0;
switch(this.mode){
case this.EQUIT:
if(Math.abs(rh)<=Proj4js.common.EPSLN){
lat=0;
}else{
lat=Math.asin(y*sinc/rh);
}
if(cosc!=0||x!=0){
lon=Math.atan2(x*sinc,cosc*rh);
}
break;
case this.OBLIQ:
if(Math.abs(rh)<=Proj4js.common.EPSLN){
lat=this.phi0;
}else{
lat=Math.asin(cosc*sinph0+y*sinc*cosph0/rh);
}
c=cosc-sinph0*Math.sin(lat);
if(c!=0||x!=0){
lon=Math.atan2(x*sinc*cosph0,c*rh);
}
break;
case this.N_POLE:
y=-y;
case this.S_POLE:
if(Math.abs(rh)<=Proj4js.common.EPSLN){
lat=this.phi0;
}else{
lat=Math.asin(this.mode==this.S_POLE?-cosc:cosc);
}
lon=(x==0&&y==0)?0:Math.atan2(x,y);
break;
}
}else{
rho=Math.sqrt(x*x+y*y);
switch(this.mode){
case this.OBLIQ:
case this.EQUIT:
tp=2*Math.atan2(rho*this.cosX1,this.akm1);
_13b=Math.cos(tp);
sinphi=Math.sin(tp);
if(rho==0){
phi_l=Math.asin(_13b*this.sinX1);
}else{
phi_l=Math.asin(_13b*this.sinX1+(y*sinphi*this.cosX1/rho));
}
tp=Math.tan(0.5*(Proj4js.common.HALF_PI+phi_l));
x*=sinphi;
y=rho*this.cosX1*_13b-y*this.sinX1*sinphi;
pi2=Proj4js.common.HALF_PI;
halfe=0.5*this.e;
break;
case this.N_POLE:
y=-y;
case this.S_POLE:
tp=-rho/this.akm1;
phi_l=Proj4js.common.HALF_PI-2*Math.atan(tp);
pi2=-Proj4js.common.HALF_PI;
halfe=-0.5*this.e;
break;
}
for(i=this.NITER;i--;phi_l=lat){
sinphi=this.e*Math.sin(phi_l);
lat=2*Math.atan(tp*Math.pow((1+sinphi)/(1-sinphi),halfe))-pi2;
if(Math.abs(phi_l-lat)<this.CONV){
if(this.mode==this.S_POLE){
lat=-lat;
}
lon=(x==0&&y==0)?0:Math.atan2(x,y);
p.x=lon;
p.y=lat;
return p;
}
}
}
}};
Proj4js.Proj.mill={init:function(){
},forward:function(p){
var lon=p.x;
var lat=p.y;
dlon=Proj4js.common.adjust_lon(lon-this.long0);
var x=this.x0+this.R*dlon;
var y=this.y0+this.R*Math.log(Math.tan((Proj4js.common.PI/4)+(lat/2.5)))*1.25;
p.x=x;
p.y=y;
return p;
},inverse:function(p){
p.x-=this.x0;
p.y-=this.y0;
var lon=Proj4js.common.adjust_lon(this.long0+p.x/this.R);
var lat=2.5*(Math.atan(Math.exp(p.y/this.R/1.25))-Proj4js.common.PI/4);
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.sinu={init:function(){
this.R=6370997;
},forward:function(p){
var x,y,delta_lon;
var lon=p.x;
var lat=p.y;
delta_lon=Proj4js.common.adjust_lon(lon-this.long0);
x=this.R*delta_lon*Math.cos(lat)+this.x0;
y=this.R*lat+this.y0;
p.x=x;
p.y=y;
return p;
},inverse:function(p){
var lat,temp,lon;
p.x-=this.x0;
p.y-=this.y0;
lat=p.y/this.R;
if(Math.abs(lat)>Proj4js.common.HALF_PI){
Proj4js.reportError("sinu:Inv:DataError");
}
temp=Math.abs(lat)-Proj4js.common.HALF_PI;
if(Math.abs(temp)>Proj4js.common.EPSLN){
temp=this.long0+p.x/(this.R*Math.cos(lat));
lon=Proj4js.common.adjust_lon(temp);
}else{
lon=this.long0;
}
p.x=lon;
p.y=lat;
return p;
}};
var GEOCENT_LAT_ERROR=1;
var COS_67P5=0.3826834323650898;
var AD_C=1.0026;
function cs_geodetic_to_geocentric(cs,p){
var _14e=p.x;
var _14f=p.y;
var _150=p.z;
var X;
var Y;
var Z;
var _154=0;
var Rn;
var _156;
var _157;
var _158;
if(_14f<-HALF_PI&&_14f>-1.001*HALF_PI){
_14f=-HALF_PI;
}else{
if(_14f>HALF_PI&&_14f<1.001*HALF_PI){
_14f=HALF_PI;
}else{
if((_14f<-HALF_PI)||(_14f>HALF_PI)){
_154|=GEOCENT_LAT_ERROR;
}
}
}
if(!_154){
if(_14e>PI){
_14e-=(2*PI);
}
_156=Math.sin(_14f);
_158=Math.cos(_14f);
_157=_156*_156;
Rn=cs.a/(Math.sqrt(1-cs.es*_157));
X=(Rn+_150)*_158*Math.cos(_14e);
Y=(Rn+_150)*_158*Math.sin(_14e);
Z=((Rn*(1-cs.es))+_150)*_156;
}
p.x=X;
p.y=Y;
p.z=Z;
return _154;
}
function cs_geocentric_to_geodetic(cs,p){
var X=p.x;
var Y=p.y;
var Z=p.z;
var _15e;
var _15f;
var _160;
var W;
var W2;
var T0;
var T1;
var S0;
var S1;
var _167;
var _168;
var _169;
var _16a;
var _16b;
var Rn;
var Sum;
var _16e;
X=parseFloat(X);
Y=parseFloat(Y);
Z=parseFloat(Z);
_16e=false;
if(X!=0){
_15e=Math.atan2(Y,X);
}else{
if(Y>0){
_15e=HALF_PI;
}else{
if(Y<0){
_15e=-HALF_PI;
}else{
_16e=true;
_15e=0;
if(Z>0){
_15f=HALF_PI;
}else{
if(Z<0){
_15f=-HALF_PI;
}else{
_15f=HALF_PI;
_160=-cs.b;
return;
}
}
}
}
}
W2=X*X+Y*Y;
W=Math.sqrt(W2);
T0=Z*AD_C;
S0=Math.sqrt(T0*T0+W2);
_167=T0/S0;
_169=W/S0;
_168=_167*_167*_167;
T1=Z+cs.b*cs.ep2*_168;
Sum=W-cs.a*cs.es*_169*_169*_169;
S1=Math.sqrt(T1*T1+Sum*Sum);
_16a=T1/S1;
_16b=Sum/S1;
Rn=cs.a/Math.sqrt(1-cs.es*_16a*_16a);
if(_16b>=COS_67P5){
_160=W/_16b-Rn;
}else{
if(_16b<=-COS_67P5){
_160=W/-_16b-Rn;
}else{
_160=Z/_16a+Rn*(cs.es-1);
}
}
if(_16e==false){
_15f=Math.atan(_16a/_16b);
}
p.x=_15e;
p.y=_15f;
p.z=_160;
return 0;
}
function cs_geocentric_to_wgs84(defn,p){
if(defn.datum_type==PJD_3PARAM){
p.x+=defn.datum_params[0];
p.y+=defn.datum_params[1];
p.z+=defn.datum_params[2];
}else{
var _171=defn.datum_params[0];
var _172=defn.datum_params[1];
var _173=defn.datum_params[2];
var _174=defn.datum_params[3];
var _175=defn.datum_params[4];
var _176=defn.datum_params[5];
var M_BF=defn.datum_params[6];
var _178=M_BF*(p.x-_176*p.y+_175*p.z)+_171;
var _179=M_BF*(_176*p.x+p.y-_174*p.z)+_172;
var _17a=M_BF*(-_175*p.x+_174*p.y+p.z)+_173;
p.x=_178;
p.y=_179;
p.z=_17a;
}
}
function cs_geocentric_from_wgs84(defn,p){
if(defn.datum_type==PJD_3PARAM){
p.x-=defn.datum_params[0];
p.y-=defn.datum_params[1];
p.z-=defn.datum_params[2];
}else{
var _17d=defn.datum_params[0];
var _17e=defn.datum_params[1];
var _17f=defn.datum_params[2];
var _180=defn.datum_params[3];
var _181=defn.datum_params[4];
var _182=defn.datum_params[5];
var M_BF=defn.datum_params[6];
var _184=(p.x-_17d)/M_BF;
var _185=(p.y-_17e)/M_BF;
var _186=(p.z-_17f)/M_BF;
p.x=_184+_182*_185-_181*_186;
p.y=-_182*_184+_185+_180*_186;
p.z=_181*_184-_180*_185+_186;
}
}
Proj4js.Proj.vandg={init:function(){
this.R=6370997;
},forward:function(p){
var lon=p.x;
var lat=p.y;
var dlon=Proj4js.common.adjust_lon(lon-this.long0);
var x,y;
if(Math.abs(lat)<=Proj4js.common.EPSLN){
x=this.x0+this.R*dlon;
y=this.y0;
}
var _18c=Proj4js.common.asinz(2*Math.abs(lat/Proj4js.common.PI));
if((Math.abs(dlon)<=Proj4js.common.EPSLN)||(Math.abs(Math.abs(lat)-Proj4js.common.HALF_PI)<=Proj4js.common.EPSLN)){
x=this.x0;
if(lat>=0){
y=this.y0+Proj4js.common.PI*this.R*Math.tan(0.5*_18c);
}else{
y=this.y0+Proj4js.common.PI*this.R*-Math.tan(0.5*_18c);
}
}
var al=0.5*Math.abs((Proj4js.common.PI/dlon)-(dlon/Proj4js.common.PI));
var asq=al*al;
var _18f=Math.sin(_18c);
var _190=Math.cos(_18c);
var g=_190/(_18f+_190-1);
var gsq=g*g;
var m=g*(2/_18f-1);
var msq=m*m;
var con=Proj4js.common.PI*this.R*(al*(g-msq)+Math.sqrt(asq*(g-msq)*(g-msq)-(msq+asq)*(gsq-msq)))/(msq+asq);
if(dlon<0){
con=-con;
}
x=this.x0+con;
con=Math.abs(con/(Proj4js.common.PI*this.R));
if(lat>=0){
y=this.y0+Proj4js.common.PI*this.R*Math.sqrt(1-con*con-2*al*con);
}else{
y=this.y0-Proj4js.common.PI*this.R*Math.sqrt(1-con*con-2*al*con);
}
p.x=x;
p.y=y;
return p;
},inverse:function(p){
var dlon;
var xx,yy,xys,c1,c2,c3;
var al,asq;
var a1;
var m1;
var con;
var th1;
var d;
p.x-=this.x0;
p.y-=this.y0;
con=Proj4js.common.PI*this.R;
xx=p.x/con;
yy=p.y/con;
xys=xx*xx+yy*yy;
c1=-Math.abs(yy)*(1+xys);
c2=c1-2*yy*yy+xx*xx;
c3=-2*c1+1+2*yy*yy+xys*xys;
d=yy*yy/c3+(2*c2*c2*c2/c3/c3/c3-9*c1*c2/c3/c3)/27;
a1=(c1-c2*c2/3/c3)/c3;
m1=2*Math.sqrt(-a1/3);
con=((3*d)/a1)/m1;
if(Math.abs(con)>1){
if(con>=0){
con=1;
}else{
con=-1;
}
}
th1=Math.acos(con)/3;
if(p.y>=0){
lat=(-m1*Math.cos(th1+Proj4js.common.PI/3)-c2/3/c3)*Proj4js.common.PI;
}else{
lat=-(-m1*Math.cos(th1+PI/3)-c2/3/c3)*Proj4js.common.PI;
}
if(Math.abs(xx)<Proj4js.common.EPSLN){
lon=this.long0;
}
lon=Proj4js.common.adjust_lon(this.long0+Proj4js.common.PI*(xys-1+Math.sqrt(1+2*(xx*xx-yy*yy)+xys*xys))/2/xx);
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.gauss={init:function(){
sphi=Math.sin(this.lat0);
cphi=Math.cos(this.lat0);
cphi*=cphi;
this.rc=Math.sqrt(1-this.es)/(1-this.es*sphi*sphi);
this.C=Math.sqrt(1+this.es*cphi*cphi/(1-this.es));
this.phic0=Math.asin(sphi/this.C);
this.ratexp=0.5*this.C*this.e;
this.K=Math.tan(0.5*this.phic0+Proj4js.common.FORTPI)/(Math.pow(Math.tan(0.5*this.lat0+Proj4js.common.FORTPI),this.C)*Proj4js.common.srat(this.e*sphi,this.ratexp));
},forward:function(p){
var lon=p.x;
var lat=p.y;
p.y=2*Math.atan(this.K*Math.pow(Math.tan(0.5*lat+Proj4js.common.FORTPI),this.C)*Proj4js.common.srat(this.e*Math.sin(lat),this.ratexp))-Proj4js.common.HALF_PI;
p.x=this.C*lon;
return p;
},inverse:function(p){
var _1a3=1e-14;
var lon=p.x/this.C;
var lat=p.y;
num=Math.pow(Math.tan(0.5*lat+Proj4js.common.FORTPI)/this.K,1/this.C);
for(var i=Proj4js.common.MAX_ITER;i>0;--i){
lat=2*Math.atan(num*Proj4js.common.srat(this.e*Math.sin(p.y),-0.5*this.e))-Proj4js.common.HALF_PI;
if(Math.abs(lat-p.y)<_1a3){
break;
}
p.y=lat;
}
if(!i){
Proj4js.reportError("gauss:inverse:convergence failed");
return null;
}
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.omerc={init:function(){
if(!this.mode){
this.mode=0;
}
if(!this.lon1){
this.lon1=0;
this.mode=1;
}
if(!this.lon2){
this.lon2=0;
}
if(!this.lat2){
this.lat2=0;
}
var temp=this.b/this.a;
var es=1-Math.pow(temp,2);
var e=Math.sqrt(es);
this.sin_p20=Math.sin(this.lat0);
this.cos_p20=Math.cos(this.lat0);
this.con=1-this.es*this.sin_p20*this.sin_p20;
this.com=Math.sqrt(1-es);
this.bl=Math.sqrt(1+this.es*Math.pow(this.cos_p20,4)/(1-es));
this.al=this.a*this.bl*this.k0*this.com/this.con;
if(Math.abs(this.lat0)<Proj4js.common.EPSLN){
this.ts=1;
this.d=1;
this.el=1;
}else{
this.ts=Proj4js.common.tsfnz(this.e,this.lat0,this.sin_p20);
this.con=Math.sqrt(this.con);
this.d=this.bl*this.com/(this.cos_p20*this.con);
if((this.d*this.d-1)>0){
if(this.lat0>=0){
this.f=this.d+Math.sqrt(this.d*this.d-1);
}else{
this.f=this.d-Math.sqrt(this.d*this.d-1);
}
}else{
this.f=this.d;
}
this.el=this.f*Math.pow(this.ts,this.bl);
}
if(this.mode!=0){
this.g=0.5*(this.f-1/this.f);
this.gama=Proj4js.common.asinz(Math.sin(this.alpha)/this.d);
this.longc=this.longc-Proj4js.common.asinz(this.g*Math.tan(this.gama))/this.bl;
this.con=Math.abs(this.lat0);
if((this.con>Proj4js.common.EPSLN)&&(Math.abs(this.con-Proj4js.common.HALF_PI)>Proj4js.common.EPSLN)){
this.singam=Math.sin(this.gama);
this.cosgam=Math.cos(this.gama);
this.sinaz=Math.sin(this.alpha);
this.cosaz=Math.cos(this.alpha);
if(this.lat0>=0){
this.u=(this.al/this.bl)*Math.atan(Math.sqrt(this.d*this.d-1)/this.cosaz);
}else{
this.u=-(this.al/this.bl)*Math.atan(Math.sqrt(this.d*this.d-1)/this.cosaz);
}
}else{
Proj4js.reportError("omerc:Init:DataError");
}
}else{
this.sinphi=Math.sin(this.at1);
this.ts1=Proj4js.common.tsfnz(this.e,this.lat1,this.sinphi);
this.sinphi=Math.sin(this.lat2);
this.ts2=Proj4js.common.tsfnz(this.e,this.lat2,this.sinphi);
this.h=Math.pow(this.ts1,this.bl);
this.l=Math.pow(this.ts2,this.bl);
this.f=this.el/this.h;
this.g=0.5*(this.f-1/this.f);
this.j=(this.el*this.el-this.l*this.h)/(this.el*this.el+this.l*this.h);
this.p=(this.l-this.h)/(this.l+this.h);
this.dlon=this.lon1-this.lon2;
if(this.dlon<-Proj4js.common.PI){
this.lon2=this.lon2-2*Proj4js.common.PI;
}
if(this.dlon>Proj4js.common.PI){
this.lon2=this.lon2+2*Proj4js.common.PI;
}
this.dlon=this.lon1-this.lon2;
this.longc=0.5*(this.lon1+this.lon2)-Math.atan(this.j*Math.tan(0.5*this.bl*this.dlon)/this.p)/this.bl;
this.dlon=Proj4js.common.adjust_lon(this.lon1-this.longc);
this.gama=Math.atan(Math.sin(this.bl*this.dlon)/this.g);
this.alpha=Proj4js.common.asinz(this.d*Math.sin(this.gama));
if(Math.abs(this.lat1-this.lat2)<=Proj4js.common.EPSLN){
Proj4js.reportError("omercInitDataError");
}else{
this.con=Math.abs(this.lat1);
}
if((this.con<=Proj4js.common.EPSLN)||(Math.abs(this.con-HALF_PI)<=Proj4js.common.EPSLN)){
Proj4js.reportError("omercInitDataError");
}else{
if(Math.abs(Math.abs(this.lat0)-Proj4js.common.HALF_PI)<=Proj4js.common.EPSLN){
Proj4js.reportError("omercInitDataError");
}
}
this.singam=Math.sin(this.gam);
this.cosgam=Math.cos(this.gam);
this.sinaz=Math.sin(this.alpha);
this.cosaz=Math.cos(this.alpha);
if(this.lat0>=0){
this.u=(this.al/this.bl)*Math.atan(Math.sqrt(this.d*this.d-1)/this.cosaz);
}else{
this.u=-(this.al/this.bl)*Math.atan(Math.sqrt(this.d*this.d-1)/this.cosaz);
}
}
},forward:function(p){
var _1ab;
var _1ac,cos_phi;
var b;
var c,t,tq;
var con,n,ml;
var q,us,vl;
var ul,vs;
var s;
var dlon;
var ts1;
var lon=p.x;
var lat=p.y;
_1ac=Math.sin(lat);
dlon=Proj4js.common.adjust_lon(lon-this.longc);
vl=Math.sin(this.bl*dlon);
if(Math.abs(Math.abs(lat)-Proj4js.common.HALF_PI)>Proj4js.common.EPSLN){
ts1=Proj4js.common.tsfnz(this.e,lat,_1ac);
q=this.el/(Math.pow(ts1,this.bl));
s=0.5*(q-1/q);
t=0.5*(q+1/q);
ul=(s*this.singam-vl*this.cosgam)/t;
con=Math.cos(this.bl*dlon);
if(Math.abs(con)<1e-7){
us=this.al*this.bl*dlon;
}else{
us=this.al*Math.atan((s*this.cosgam+vl*this.singam)/con)/this.bl;
if(con<0){
us=us+Proj4js.common.PI*this.al/this.bl;
}
}
}else{
if(lat>=0){
ul=this.singam;
}else{
ul=-this.singam;
}
us=this.al*lat/this.bl;
}
if(Math.abs(Math.abs(ul)-1)<=Proj4js.common.EPSLN){
Proj4js.reportError("omercFwdInfinity");
}
vs=0.5*this.al*Math.log((1-ul)/(1+ul))/this.bl;
us=us-this.u;
var x=this.x0+vs*this.cosaz+us*this.sinaz;
var y=this.y0+us*this.cosaz-vs*this.sinaz;
p.x=x;
p.y=y;
return p;
},inverse:function(p){
var _1ba;
var _1bb;
var _1bc;
var _1bd,cos_phi;
var b;
var c,t,tq;
var con,n,ml;
var vs,us,q,s,ts1;
var vl,ul,bs;
var dlon;
var flag;
p.x-=this.x0;
p.y-=this.y0;
flag=0;
vs=p.x*this.cosaz-p.y*this.sinaz;
us=p.y*this.cosaz+p.x*this.sinaz;
us=us+this.u;
q=Math.exp(-this.bl*vs/this.al);
s=0.5*(q-1/q);
t=0.5*(q+1/q);
vl=Math.sin(this.bl*us/this.al);
ul=(vl*this.cosgam+s*this.singam)/t;
if(Math.abs(Math.abs(ul)-1)<=Proj4js.common.EPSLN){
lon=this.longc;
if(ul>=0){
lat=Proj4js.common.HALF_PI;
}else{
lat=-Proj4js.common.HALF_PI;
}
}else{
con=1/this.bl;
ts1=Math.pow((this.el/Math.sqrt((1+ul)/(1-ul))),con);
lat=Proj4js.common.phi2z(this.e,ts1);
_1bb=this.longc-Math.atan2((s*this.cosgam-vl*this.singam),con)/this.bl;
lon=Proj4js.common.adjust_lon(_1bb);
}
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.lcc={init:function(){
if(!this.lat2){
this.lat2=this.lat0;
}
if(!this.k0){
this.k0=1;
}
if(Math.abs(this.lat1+this.lat2)<Proj4js.common.EPSLN){
Proj4js.reportError("lcc:init: Equal Latitudes");
return;
}
var temp=this.b/this.a;
this.e=Math.sqrt(1-temp*temp);
var sin1=Math.sin(this.lat1);
var cos1=Math.cos(this.lat1);
var ms1=Proj4js.common.msfnz(this.e,sin1,cos1);
var ts1=Proj4js.common.tsfnz(this.e,this.lat1,sin1);
var sin2=Math.sin(this.lat2);
var cos2=Math.cos(this.lat2);
var ms2=Proj4js.common.msfnz(this.e,sin2,cos2);
var ts2=Proj4js.common.tsfnz(this.e,this.lat2,sin2);
var ts0=Proj4js.common.tsfnz(this.e,this.lat0,Math.sin(this.lat0));
if(Math.abs(this.lat1-this.lat2)>Proj4js.common.EPSLN){
this.ns=Math.log(ms1/ms2)/Math.log(ts1/ts2);
}else{
this.ns=sin1;
}
this.f0=ms1/(this.ns*Math.pow(ts1,this.ns));
this.rh=this.a*this.f0*Math.pow(ts0,this.ns);
if(!this.title){
this.title="Lambert Conformal Conic";
}
},forward:function(p){
var lon=p.x;
var lat=p.y;
if(lat<=90&&lat>=-90&&lon<=180&&lon>=-180){
}else{
Proj4js.reportError("lcc:forward: llInputOutOfRange: "+lon+" : "+lat);
return null;
}
var con=Math.abs(Math.abs(lat)-Proj4js.common.HALF_PI);
var ts;
if(con>Proj4js.common.EPSLN){
ts=Proj4js.common.tsfnz(this.e,lat,Math.sin(lat));
rh1=this.a*this.f0*Math.pow(ts,this.ns);
}else{
con=lat*this.ns;
if(con<=0){
Proj4js.reportError("lcc:forward: No Projection");
return null;
}
rh1=0;
}
var _1d4=this.ns*Proj4js.common.adjust_lon(lon-this.long0);
p.x=this.k0*(rh1*Math.sin(_1d4))+this.x0;
p.y=this.k0*(this.rh-rh1*Math.cos(_1d4))+this.y0;
return p;
},inverse:function(p){
var rh1,con,ts;
var lat,lon;
x=(p.x-this.x0)/this.k0;
y=(this.rh-(p.y-this.y0)/this.k0);
if(this.ns>0){
rh1=Math.sqrt(x*x+y*y);
con=1;
}else{
rh1=-Math.sqrt(x*x+y*y);
con=-1;
}
var _1d8=0;
if(rh1!=0){
_1d8=Math.atan2((con*x),(con*y));
}
if((rh1!=0)||(this.ns>0)){
con=1/this.ns;
ts=Math.pow((rh1/(this.a*this.f0)),con);
lat=Proj4js.common.phi2z(this.e,ts);
if(lat==-9999){
return null;
}
}else{
lat=-Proj4js.common.HALF_PI;
}
lon=Proj4js.common.adjust_lon(_1d8/this.ns+this.long0);
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.laea={init:function(){
this.sin_lat_o=Math.sin(this.lat0);
this.cos_lat_o=Math.cos(this.lat0);
},forward:function(p){
var lon=p.x;
var lat=p.y;
var _1dc=Proj4js.common.adjust_lon(lon-this.long0);
var _1dd=Math.sin(lat);
var _1de=Math.cos(lat);
var _1df=Math.sin(_1dc);
var _1e0=Math.cos(_1dc);
var g=this.sin_lat_o*_1dd+this.cos_lat_o*_1de*_1e0;
if(g==-1){
Proj4js.reportError("laea:fwd:Point projects to a circle of radius "+2*R);
return null;
}
var ksp=this.a*Math.sqrt(2/(1+g));
var x=ksp*_1de*_1df+this.x0;
var y=ksp*(this.cos_lat_o*_1dd-this.sin_lat_o*_1de*_1e0)+this.x0;
p.x=x;
p.y=y;
return p;
},inverse:function(p){
p.x-=this.x0;
p.y-=this.y0;
var Rh=Math.sqrt(p.x*p.x+p.y*p.y);
var temp=Rh/(2*this.a);
if(temp>1){
Proj4js.reportError("laea:Inv:DataError");
return null;
}
var z=2*Proj4js.common.asinz(temp);
var _1e9=Math.sin(z);
var _1ea=Math.cos(z);
var lon=this.long0;
if(Math.abs(Rh)>Proj4js.common.EPSLN){
var lat=Proj4js.common.asinz(this.sin_lat_o*_1ea+this.cos_lat_o*_1e9*p.y/Rh);
var temp=Math.abs(this.lat0)-Proj4js.common.HALF_PI;
if(Math.abs(temp)>Proj4js.common.EPSLN){
temp=_1ea-this.sin_lat_o*Math.sin(lat);
if(temp!=0){
lon=Proj4js.common.adjust_lon(this.long0+Math.atan2(p.x*_1e9*this.cos_lat_o,temp*Rh));
}
}else{
if(this.lat0<0){
lon=Proj4js.common.adjust_lon(this.long0-Math.atan2(-p.x,p.y));
}else{
lon=Proj4js.common.adjust_lon(this.long0+Math.atan2(p.x,-p.y));
}
}
}else{
lat=this.lat0;
}
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.aeqd={init:function(){
this.sin_p12=Math.sin(this.lat0);
this.cos_p12=Math.cos(this.lat0);
},forward:function(p){
var lon=p.x;
var lat=p.y;
var ksp;
var _1f1=Math.sin(p.y);
var _1f2=Math.cos(p.y);
var dlon=Proj4js.common.adjust_lon(lon-this.long0);
var _1f4=Math.cos(dlon);
var g=this.sin_p12*_1f1+this.cos_p12*_1f2*_1f4;
if(Math.abs(Math.abs(g)-1)<Proj4js.common.EPSLN){
ksp=1;
if(g<0){
Proj4js.reportError("aeqd:Fwd:PointError");
return;
}
}else{
var z=Math.acos(g);
ksp=z/Math.sin(z);
}
p.x=this.x0+this.a*ksp*_1f2*Math.sin(dlon);
p.y=this.y0+this.a*ksp*(this.cos_p12*_1f1-this.sin_p12*_1f2*_1f4);
return p;
},inverse:function(p){
p.x-=this.x0;
p.y-=this.y0;
var rh=Math.sqrt(p.x*p.x+p.y*p.y);
if(rh>(2*Proj4js.common.HALF_PI*this.a)){
Proj4js.reportError("aeqdInvDataError");
return;
}
var z=rh/this.a;
var sinz=Math.sin(z);
var cosz=Math.cos(z);
var lon=this.long0;
var lat;
if(Math.abs(rh)<=Proj4js.common.EPSLN){
lat=this.lat0;
}else{
lat=Proj4js.common.asinz(cosz*this.sin_p12+(p.y*sinz*this.cos_p12)/rh);
var con=Math.abs(this.lat0)-Proj4js.common.HALF_PI;
if(Math.abs(con)<=Proj4js.common.EPSLN){
if(lat0>=0){
lon=Proj4js.common.adjust_lon(this.long0+Math.atan2(p.x,-p.y));
}else{
lon=Proj4js.common.adjust_lon(this.long0-Math.atan2(-p.x,p.y));
}
}else{
con=cosz-this.sin_p12*Math.sin(lat);
if((Math.abs(con)<Proj4js.common.EPSLN)&&(Math.abs(p.x)<Proj4js.common.EPSLN)){
}else{
var temp=Math.atan2((p.x*sinz*this.cos_p12),(con*rh));
lon=Proj4js.common.adjust_lon(this.long0+Math.atan2((p.x*sinz*this.cos_p12),(con*rh)));
}
}
}
p.x=lon;
p.y=lat;
return p;
}};
Proj4js.Proj.moll={init:function(){
},forward:function(p){
var lon=p.x;
var lat=p.y;
var _203=Proj4js.common.adjust_lon(lon-this.long0);
var _204=lat;
var con=Proj4js.common.PI*Math.sin(lat);
for(var i=0;;i++){
var _207=-(_204+Math.sin(_204)-con)/(1+Math.cos(_204));
_204+=_207;
if(Math.abs(_207)<Proj4js.common.EPSLN){
break;
}
if(i>=50){
Proj4js.reportError("moll:Fwd:IterationError");
}
}
_204/=2;
if(Proj4js.common.PI/2-Math.abs(lat)<Proj4js.common.EPSLN){
_203=0;
}
var x=0.900316316158*this.R*_203*Math.cos(_204)+this.x0;
var y=1.4142135623731*this.R*Math.sin(_204)+this.y0;
p.x=x;
p.y=y;
return p;
},inverse:function(p){
var _20b;
var arg;
p.x-=this.x0;
var arg=p.y/(1.4142135623731*this.R);
if(Math.abs(arg)>0.999999999999){
arg=0.999999999999;
}
var _20b=Math.asin(arg);
var lon=Proj4js.common.adjust_lon(this.long0+(p.x/(0.900316316158*this.R*Math.cos(_20b))));
if(lon<(-Proj4js.common.PI)){
lon=-Proj4js.common.PI;
}
if(lon>Proj4js.common.PI){
lon=Proj4js.common.PI;
}
arg=(2*_20b+Math.sin(2*_20b))/Proj4js.common.PI;
if(Math.abs(arg)>1){
arg=1;
}
var lat=Math.asin(arg);
p.x=lon;
p.y=lat;
return p;
}};

var MB_IS_MOZ=(document.implementation&&document.implementation.createDocument)?true:false;
function XslProcessor(_1,_2){
if(!MB_IS_MOZ){
_SARISSA_DOM_PROGID=Sarissa.pickRecentProgID(["Msxml2.DOMDocument.3.0","Msxml2.DOMDocument.6.0"],[["SELECT_NODES",2],["TRANSFORM_NODE",2]]);
_SARISSA_XMLHTTP_PROGID=Sarissa.pickRecentProgID(["Msxml2.XMLHTTP.3.0","MSXML2.XMLHTTP.6.0"],[["XMLHTTP",4]]);
_SARISSA_THREADEDDOM_PROGID=Sarissa.pickRecentProgID(["Msxml2.FreeThreadedDOMDocument.3.0","MSXML2.FreeThreadedDOMDocument.6.0"]);
_SARISSA_XSLTEMPLATE_PROGID=Sarissa.pickRecentProgID(["Msxml2.XSLTemplate.3.0","MSXML2.XSLTemplate.6.0"],[["XSLTPROC",2]]);
}
this.xslUrl=_1;
this.xslDom=Sarissa.getDomDocument();
this.xslDom.async=false;
if(!MB_IS_MOZ){
try{
this.xslDom.validateOnParse=false;
this.xslDom.setProperty("AllowDocumentFunction",true);
this.xslDom.resolveExternals=true;
}
catch(e){
}
}
if(typeof (inlineXSL)!="undefined"){
xmlString=inlineXSL[_1];
xmlString=xmlString.replace(/DOUBLE_QUOTE/g,"\"");
this.xslDom=(new DOMParser()).parseFromString(xmlString,"text/xml");
}else{
if(_SARISSA_IS_SAFARI){
var _3=new XMLHttpRequest();
_3.open("GET",_1,false);
_3.send(null);
this.xslDom=_3.responseXML;
}else{
this.xslDom.load(_1);
}
}
if(Sarissa.getParseErrorText(this.xslDom)!=Sarissa.PARSED_OK){
alert(mbGetMessage("errorLoadingStylesheet",_1));
}
this.processor=new XSLTProcessor();
this.processor.importStylesheet(this.xslDom);
this.docNSUri=_2;
this.transformNodeToString=function(_4){
try{
if(_SARISSA_IS_IE){
var _5=(new XMLSerializer()).serializeToString(_4);
var _4=(new DOMParser()).parseFromString(_5,"text/xml");
}
var _6=this.transformNodeToObject(_4);
var s=(new XMLSerializer()).serializeToString(_6);
if(_SARISSA_IS_OPERA){
s=s.replace(/.*\?\>/,"");
}
return s;
}
catch(e){
alert(mbGetMessage("exceptionTransformingDoc",this.xslUrl));
alert("XSL="+(new XMLSerializer()).serializeToString(this.xslDom));
alert("XML="+(new XMLSerializer()).serializeToString(_4));
}
};
this.transformNodeToObject=function(_8){
if(_SARISSA_IS_SAFARI){
var _9=new DOMParser().parseFromString("<xsltresult></xsltresult>","text/xml");
var _a=this.processor.transformToFragment(_8,_9);
var _b=(new XMLSerializer()).serializeToString(_a);
_b.replace(/\s/g,"");
}else{
var _a=this.processor.transformToDocument(_8);
}
return _a;
};
this.setParameter=function(_c,_d,_e){
if(!_d){
return;
}
this.processor.setParameter(null,_c,_d);
};
}
function postLoad(_f,_10,_11){
var _12=new XMLHttpRequest();
if(_f.indexOf("http://")==0||_f.indexOf("https://")==0){
_12.open("POST",config.proxyUrl,false);
_12.setRequestHeader("serverUrl",_f);
}else{
_12.open("POST",_f,false);
}
_12.setRequestHeader("content-type","text/xml");
if(_11){
_12.setRequestHeader("content-type",_11);
}
_12.send(_10);
if(_12.status>=400){
alert(mbGetMessage("errorLoadingDocument",_f,_12.statusText,_12.responseText));
var _13=Sarissa.getDomDocument();
_13.parseError=-1;
return _13;
}else{
if(null==_12.responseXML){
alert(mbGetMessage("nullXmlResponse",_12.responseText));
}
return _12.responseXML;
}
}
function postGetLoad(_14,_15,_16,dir,_18){
var _19=new XMLHttpRequest();
var _1a=_14.indexOf("?")==-1?"?":"&";
if(dir&&_18){
_14=_14+_1a+"dir="+dir+"&fileName="+_18;
}else{
if(dir){
_14=_14+_1a+"dir="+dir;
}else{
if(_18){
_14=_14+_1a+"fileName="+_18;
}
}
}
if(_14.indexOf("http://")==0||_14.indexOf("https://")==0){
_19.open("POST",config.proxyUrl,false);
_19.setRequestHeader("serverUrl",_14);
}else{
_19.open("POST",_14,false);
}
_19.setRequestHeader("content-type","text/xml");
if(_16){
_19.setRequestHeader("content-type",_16);
}
_19.send(_15);
if(_19.status>=400){
alert(mbGetMessage("errorLoadingDocument",_14,_19.statusText,_19.responseText));
var _1b=Sarissa.getDomDocument();
_1b.parseError=-1;
return _1b;
}else{
if(null==_19.responseXML){
alert(mbGetMessage("nullXmlResponse",_19.responseText));
}
return _19.responseXML;
}
}
function getProxyPlusUrl(url){
if(url.indexOf("http://")==0||url.indexOf("https://")==0){
if(config.proxyUrl){
url=config.proxyUrl+"?url="+escape(url).replace(/\+/g,"%2C").replace(/\"/g,"%22").replace(/\'/g,"%27");
}else{
alert(mbGetMessage("unableToLoadDoc",url));
url=null;
}
}
return url;
}
function createElementWithNS(doc,_1e,_1f){
if(_SARISSA_IS_IE){
return doc.createNode(1,_1e,_1f);
}else{
return doc.createElementNS(_1f,_1e);
}
}
function UniqueId(){
this.lastId=0;
this.getId=function(){
this.lastId++;
return this.lastId;
};
}
var mbIds=new UniqueId();
function setISODate(_20){
var _21=_20.match(/(\d{4})-?(\d{2})?-?(\d{2})?T?(\d{2})?:?(\d{2})?:?(\d{2})?\.?(\d{0,3})?(Z)?/);
var res=null;
for(var i=1;i<_21.length;++i){
if(!_21[i]){
_21[i]=(i==3)?1:0;
if(!res){
res=i;
}
}
}
var _24=new Date();
_24.setFullYear(parseInt(_21[1],10));
_24.setMonth(parseInt(_21[2],10)-1,parseInt(_21[3],10));
_24.setDate(parseInt(_21[3],10));
_24.setHours(parseInt(_21[4],10));
_24.setMinutes(parseInt(_21[5],10));
_24.setSeconds(parseFloat(_21[6],10));
if(!res){
res=6;
}
_24.res=res;
return _24;
}
function getISODate(_25){
var res=_25.res?_25.res:6;
var _27="";
_27+=res>=1?_25.getFullYear():"";
_27+=res>=2?"-"+leadingZeros(_25.getMonth()+1,2):"";
_27+=res>=3?"-"+leadingZeros(_25.getDate(),2):"";
_27+=res>=4?"T"+leadingZeros(_25.getHours(),2):"";
_27+=res>=5?":"+leadingZeros(_25.getMinutes(),2):"";
_27+=res>=6?":"+leadingZeros(_25.getSeconds(),2):"";
return _27;
}
function leadingZeros(num,_29){
var _2a=parseInt(num,10);
var _2b=Math.pow(10,_29);
if(_2a<_2b){
_2a+=_2b;
}
return _2a.toString().substr(1);
}
function fixPNG(_2c,_2d,_2e){
if(_SARISSA_IS_IE){
if(_2e){
var _2f=_2e.style.filter.substring(_2e.style.filter.indexOf("opacity=",0)+8,_2e.style.filter.lastIndexOf(")",0));
if(_2e.style.filter.indexOf("opacity=",0)==-1){
_2f=null;
}
var _30=(_2f)?_2f/100:-1;
}
var _31="id='"+_2d+"' ";
var _32=(_2c.className)?"class='"+_2c.className+"' ":"";
var _33=(_2c.title)?"title='"+_2c.title+"' ":"title='"+_2c.alt+"' ";
var _34="display:inline-block;"+_2c.style.cssText;
var _35="<span "+_31+_32+_33;
_35+=" style=\""+"width:"+_2c.width+"px; height:"+_2c.height+"px;"+_34+";";
if(_30!=-1){
_35+="opacity="+_30+";";
}
var src=_2c.src;
src=src.replace(/\(/g,"%28");
src=src.replace(/\)/g,"%29");
src=src.replace(/'/g,"%27");
src=src.replace(/%23/g,"%2523");
_35+="filter:progid:DXImageTransform.Microsoft.AlphaImageLoader";
_35+="(src='"+src+"', sizingMethod='scale')";
if(_2e&&_30!=-1){
_35+=" alpha(opacity="+(_30*100)+")";
}
_35+="; \"></span>";
return _35;
}
}
function getAbsX(elt){
return (elt.x)?elt.x:getAbsPos(elt,"Left")+2;
}
function getAbsY(elt){
return (elt.y)?elt.y:getAbsPos(elt,"Top")+2;
}
function getAbsPos(elt,_3a){
iPos=0;
while(elt!=null){
iPos+=elt["offset"+_3a];
elt=elt.offsetParent;
}
return iPos;
}
function getPageX(e){
var _3c=0;
if(!e){
var e=window.event;
}
if(e.pageX){
_3c=e.pageX;
}else{
if(e.clientX){
_3c=e.clientX;
}
}
if(document.body&&document.body.scrollLeft){
_3c+=document.body.scrollLeft;
}else{
if(document.documentElement&&document.documentElement.scrollLeft){
_3c+=document.documentElement.scrollLeft;
}
}
return _3c;
}
function getPageY(e){
var _3e=0;
if(!e){
var e=window.event;
}
if(e.pageY){
_3e=e.pageY;
}else{
if(e.clientY){
_3e=e.clientY;
}
}
if(document.body&&document.body.scrollTop){
_3e+=document.body.scrollTop;
}else{
if(document.documentElement&&document.documentElement.scrollTop){
_3e+=document.documentElement.scrollTop;
}
}
return _3e;
}
function getArgs(){
var _3f=new Object();
var _40=location.search.substring(1);
var _41=_40.split("&");
for(var i=0;i<_41.length;i++){
var pos=_41[i].indexOf("=");
if(pos==-1){
continue;
}
var _44=_41[i].substring(0,pos);
var _45=_41[i].substring(pos+1);
_3f[_44]=unescape(_45.replace(/\+/g," "));
}
return _3f;
}
window.cgiArgs=getArgs();
function getScreenX(_46,_47){
bbox=_46.getBoundingBox();
width=_46.getWindowWidth();
bbox[0]=parseFloat(bbox[0]);
bbox[2]=parseFloat(bbox[2]);
var _48=(width/(bbox[2]-bbox[0]));
x=_48*(_47-bbox[0]);
return x;
}
function getScreenY(_49,_4a){
var _4b=_49.getBoundingBox();
var _4c=_49.getWindowHeight();
_4b[1]=parseFloat(_4b[1]);
_4b[3]=parseFloat(_4b[3]);
var _4d=(heighteight/(_4b[3]-_4b[1]));
var y=_4c-(_4d*(pt.y-_4b[1]));
return y;
}
function getGeoCoordX(_4f,_50){
var _51=_4f.getBoundingBox();
var _52=_4f.getWindowWidth();
_51[0]=parseFloat(_51[0]);
_51[2]=parseFloat(_51[2]);
var _53=((_51[2]-_51[0])/_52);
var x=_51[0]+_53*(xCoord);
return x;
}
function getGeoCoordY(_55){
var _56=context.getBoundingBox();
var _57=context.getWindowHeight();
_56[1]=parseFloat(_56[1]);
_56[3]=parseFloat(_56[3]);
var _58=((_56[3]-_56[1])/_57);
var y=_56[1]+_58*(_57-_55);
return y;
}
function makeElt(_5a){
var _5b=document.createElement(_5a);
document.getElementsByTagName("body").item(0).appendChild(_5b);
return _5b;
}
var newWindow="";
function openPopup(url,_5d,_5e){
if(_5d==null){
_5d=300;
}
if(_5e==null){
_5e=200;
}
if(!newWindow.closed&&newWindow.location){
newWindow.location.href=url;
}else{
newWindow=window.open(url,"name","height="+_5e+",width="+_5d);
if(!newWindow.opener){
newwindow.opener=self;
}
}
if(window.focus){
newWindow.focus();
}
return false;
}
function debug(_5f){
tarea=makeElt("textarea");
tarea.setAttribute("rows","3");
tarea.setAttribute("cols","40");
tnode=document.createTextNode(_5f);
tarea.appendChild(tnode);
}
function returnTarget(evt){
evt=(evt)?evt:((window.event)?window.event:"");
var elt=null;
if(evt.target){
elt=evt.target;
}else{
if(evt.srcElement){
elt=evt.srcElement;
}
}
return elt;
}
function addEvent(_62,_63,_64){
if(document.addEventListener){
_62.addEventListener(_63,_64,false);
}else{
if(document.attachEvent){
_62.attachEvent("on"+_63,_64);
}
}
}
function handleEventWithObject(evt){
var elt=returnTarget(evt);
var obj=elt.ownerObj;
if(obj!=null){
obj.handleEvent(evt);
}
}
function mbDebugMessage(_68,_69){
if(_68&&_68.debug){
alert(_69);
}
}
function mbGetMessage(_6a){
var _6b="NoMsgsFound";
if(config.widgetText){
var _6c="/mb:WidgetText/mb:messages/mb:"+_6a;
var _6d=config.widgetText.selectNodes(_6c);
if(!_6d||_6d.length==0){
_6b=_6a;
}else{
_6b=getNodeValue(_6d.item(_6d.length-1));
if(arguments[mbGetMessage.length]){
var _6e=[].slice.call(arguments,mbGetMessage.length);
_6e.unshift(_6b);
_6b=mbFormatMessage.apply(this,_6e);
}
}
}
return _6b;
}
function mbFormatMessage(_6f){
var _70=_6f;
var _71=[].slice.call(arguments,mbFormatMessage.length);
for(var i=0;i<_71.length;i++){
var _73=new RegExp("\\{"+i+"\\}","g");
_70=_70.replace(_73,_71[i]);
}
return _70;
}
function sld2UrlParam(_74){
var _75=new Array();
if(_74){
var sld=_74.selectSingleNode("wmc:SLD");
var _77=_74.selectSingleNode("wmc:Name");
if(sld){
if(sld.selectSingleNode("wmc:OnlineResource")){
_75.sld=sld.selectSingleNode("wmc:OnlineResource").getAttribute("xlink:href");
}else{
if(sld.selectSingleNode("wmc:FeatureTypeStyle")){
_75.sld=(new XMLSerializer()).serializeToString(sld.selectSingleNode("wmc:FeatureTypeStyle"));
}else{
if(sld.selectSingleNode("wmc:StyledLayerDescriptor")){
_75.sld_body=(new XMLSerializer()).serializeToString(sld.selectSingleNode("wmc:StyledLayerDescriptor"));
}
}
}
}else{
if(_77){
_75.styles=getNodeValue(_77)||"";
}
}
}
return _75;
}
function sld2OlStyle(_78){
if(_78){
var _79=_78.selectSingleNode("wmc:SLD/sld:FeatureTypeStyle");
if(_79){
var sld=new XMLSerializer().serializeToString(_79);
var _7b=/<([^:]*:?)StyledLayerDescriptor/;
if(!_7b.test(sld)){
sld=sld.replace(/<([^:]*:?)FeatureTypeStyle([^>]*)>(.*)$/,"<$1StyledLayerDescriptor$2><$1NamedLayer><$1Name>sld</$1Name><$1UserStyle><$1FeatureTypeStyle>$3</$1UserStyle></$1NamedLayer></$1StyledLayerDescriptor>");
}
}
var _7c=new OpenLayers.Format.SLD().read(sld);
if(_7c){
return _7c.namedLayers["sld"].userStyles[0];
}
}
var _7d={fillColor:"#808080",fillOpacity:1,strokeColor:"#000000",strokeOpacity:1,strokeWidth:1,pointRadius:6};
var _7e=OpenLayers.Util.extend(_7d,OpenLayers.Feature.Vector.style["default"]);
var _7f;
var _80=false;
if(_78){
_7f=_78.selectSingleNode(".//sld:ExternalGraphic/sld:OnlineResource/@xlink:href");
if(_7f){
_7e.externalGraphic=getNodeValue(_7f);
_80=true;
}
_7f=_78.selectSingleNode(".//sld:Fill/sld:CssParameter[@name='fill']");
if(_7f){
_7e.fillColor=getNodeValue(_7f);
_80=true;
}
_7f=_78.selectSingleNode(".//sld:Fill/sld:CssParameter[@name='fill-opacity']");
if(_7f){
_7e.fillOpacity=getNodeValue(_7f);
_80=true;
}else{
_7f=_78.selectSingleNode(".//sld:Opacity/sld:Literal");
if(_7f){
_7e.fillOpacity=getNodeValue(_7f);
_80=true;
}
}
_7f=_78.selectSingleNode(".//sld:Stroke/sld:CssParameter[@name='stroke']");
if(_7f){
_7e.strokeColor=getNodeValue(_7f);
_80=true;
}
_7f=_78.selectSingleNode(".//sld:Stroke/sld:CssParameter[@name='stroke-opacity']");
if(_7f){
_7e.strokeOpacity=getNodeValue(_7f);
_80=true;
}
_7f=_78.selectSingleNode(".//sld:Stroke/sld:CssParameter[@name='stroke-width']");
if(_7f){
_7e.strokeWidth=getNodeValue(_7f);
_80=true;
}
_7f=_78.selectSingleNode(".//sld:Size");
if(_7f){
_7e.pointRadius=getNodeValue(_7f);
_80=true;
}
}
if(!_80){
_7e=null;
}
return _7e;
}
function loadCss(_81){
if(typeof config=="undefined"||typeof config.skinDir!="string"){
if(!mapbuilder.cssToLoad){
mapbuilder.cssToLoad=[];
}
mapbuilder.cssToLoad.push(_81);
return;
}
var id=_81.match(/[^\/]*$/).toString().replace(/./,"_");
if(!document.getElementById(id)){
var _83=document.createElement("link");
_83.setAttribute("id",id);
_83.setAttribute("rel","stylesheet");
_83.setAttribute("type","text/css");
_83.setAttribute("href",config.skinDir+"/"+_81);
document.getElementsByTagName("head")[0].appendChild(_83);
}
}
function getNodeValue(_84){
if(_84.nodeType==1){
return _84.firstChild?_84.firstChild.nodeValue:"";
}
if(_84.nodeType<5){
return _84.nodeValue;
}
return _84;
}
Mapbuilder.getProperty=function(_85,_86,_87){
if(typeof _87=="undefined"){
_87=null;
}
var _88=_85.selectSingleNode(_86);
return _88?getNodeValue(_88):_87;
};
Mapbuilder.parseBoolean=function(_89){
var _8a=false;
if(!_89){
_8a=false;
}else{
if(_89==0){
_8a=false;
}else{
if(_89==1){
_8a=true;
}else{
if(_89.match(/true/i)){
_8a=true;
}else{
if(_89.match(/false/i)){
return false;
}
}
}
}
}
return _8a;
};

function Listener(){
this.listeners=new Array();
this.values=new Array();
this.addListener=function(_1,_2,_3){
if(window.logger){
logger.logEvent("addListener: "+_1,this.id,_3.id);
}
if(!this.listeners[_1]){
this.listeners[_1]=new Array();
}
this.removeListener(_1,_2,_3);
this.listeners[_1].push(new Array(_2,_3));
if(!_2){
alert(mbGetMessage("undefinedListener",_3.id));
}
};
this.addFirstListener=function(_4,_5,_6){
if(window.logger){
logger.logEvent("addFirstListener: "+_4,this.id,_6.id);
}
if(!this.listeners[_4]){
this.listeners[_4]=new Array();
}
this.removeListener(_4,_5,_6);
this.listeners[_4].unshift(new Array(_5,_6));
if(!_5){
alert(mbGetMessage("undefinedListener",_6));
}
};
this.removeListener=function(_7,_8,_9){
if(this.listeners[_7]){
for(var i=0;i<this.listeners[_7].length;i++){
if(this.listeners[_7][i][0]==_8&&this.listeners[_7][i][1]==_9){
for(var j=i;j<this.listeners[_7].length-1;j++){
this.listeners[_7][j]=this.listeners[_7][j+1];
}
this.listeners[_7].pop();
return;
}
}
}
};
this.callListeners=function(_c,_d){
if(this.listeners[_c]){
var _e=this.listeners[_c].length;
for(var i=0;i<_e;i++){
if(window.logger){
logger.logEvent(_c,this.id,this.listeners[_c][i][1].id,_d);
}
if(this.listeners[_c][i][0]){
this.listeners[_c][i][0](this.listeners[_c][i][1],_d);
}else{
alert(mbGetMessage("listenerError",_c,this.listeners[_c][i][1].id,this.listeners[_c][i][0]));
}
}
}
};
this.setParam=function(_10,_11){
this.values[_10]=_11;
this.callListeners(_10,_11);
};
this.getParam=function(_12){
return this.values[_12];
};
}

mapbuilder.loadScript(baseDir+"/util/Listener.js");
function ModelBase(_1,_2){
Listener.apply(this);
this.async=true;
this.contentType="text/xml";
this.modelNode=_1;
var _3=_1.attributes.getNamedItem("id");
if(_3){
this.id=_3.nodeValue;
}else{
this.id="MbModel_"+mbIds.getId();
}
this.getProperty=function(_4,_5){
return Mapbuilder.getProperty(_1,_4,_5);
};
this.title=this.getProperty("mb:title",this.id);
this.debug=Mapbuilder.parseBoolean(this.getProperty("mb:debug",false));
if(window.cgiArgs[this.id]){
this.url=window.cgiArgs[this.id];
}else{
if(window[this.id]&&typeof window[this.id]=="string"){
this.url=window[this.id];
}else{
if(_1.url){
this.url=_1.url;
}else{
var _6=_1.selectSingleNode("mb:defaultModelUrl");
if(_6){
this.url=getNodeValue(_6);
}
}
}
}
this.method=this.getProperty("mb:method","get");
this.namespace=this.getProperty("mb:namespace");
var _7=_1.attributes.getNamedItem("template");
if(_7){
this.template=Mapbuilder.parseBoolean(_7.nodeValue);
this.modelNode.removeAttribute("template");
}
this.nodeSelectXpath=this.getProperty("mb:nodeSelectXpath");
this.config=new Array();
this.getXpathValue=function(_8,_9){
if(!_8.doc){
return null;
}
node=_8.doc.selectSingleNode(_9);
if(node&&node.firstChild){
return getNodeValue(node);
}else{
return null;
}
};
this.setXpathValue=function(_a,_b,_c,_d){
if(_d==null){
_d=true;
}
var _e=_a.doc.selectSingleNode(_b);
if(_e){
if(_e.firstChild){
_e.firstChild.nodeValue=_c;
}else{
dom=Sarissa.getDomDocument();
v=dom.createTextNode(_c);
_e.appendChild(v);
}
if(_d){
_a.setParam("refresh");
}
return true;
}else{
return false;
}
};
this.loadModelDoc=function(_f){
if(_f.url){
_f.callListeners("newModel");
_f.setParam("modelStatus","loading");
if(_f.contentType=="image"){
_f.doc=new Image();
_f.doc.src=_f.url;
}else{
var _10=new XMLHttpRequest();
var _11=_f.url;
if(_11.indexOf("http://")==0||_11.indexOf("https://")==0){
if(_f.method.toLowerCase()=="get"){
_11=getProxyPlusUrl(_11);
}else{
_11=config.proxyUrl;
}
}
_10.open(_f.method,_11,_f.async);
if(_f.method.toLowerCase()=="post"){
_10.setRequestHeader("content-type",_f.contentType);
_10.setRequestHeader("serverUrl",_f.url);
}
_10.onreadystatechange=function(){
_f.setParam("modelStatus",httpStatusMsg[_10.readyState]);
if(_10.readyState==4){
if(_10.status>=400){
var _12=mbGetMessage("errorLoadingDocument",_11,_10.statusText,_10.responseText);
alert(_12);
_f.setParam("modelStatus",_12);
return;
}else{
if((_10.responseXML!=null)&&(_10.responseXML.root!=null)&&(_10.responseXML.root.children.length>0)){
_f.doc=_10.responseXML;
if(Sarissa.getParseErrorText(_f.doc)==Sarissa.PARSED_OK){
_f.finishLoading();
}else{
alert(mbGetMessage("parseError",Sarissa.getParseErrorText(_f.doc)));
}
return;
}
if(_10.responseText!=null){
_f.doc=Sarissa.getDomDocument();
_f.doc.async=false;
_f.doc=(new DOMParser()).parseFromString(_10.responseText.replace(/>\s+</g,"><"),"text/xml");
if(_f.doc==null){
alert(mbGetMessage("documentParseError",Sarissa.getParseErrorText(_f.doc)));
}else{
if(Sarissa.getParseErrorText(_f.doc)==Sarissa.PARSED_OK){
_f.finishLoading();
}else{
alert(mbGetMessage("parseError",Sarissa.getParseErrorText(_f.doc)));
}
}
return;
}
}
}
};
var _13=_f.postData||"";
if(typeof _13=="object"){
_13=new XMLSerializer().serializeToString(_13);
}
_10.send(_13);
if(!_f.async){
if(_10.status>=400){
var _14=mbGetMessage("errorLoadingDocument",_11,_10.statusText,_10.responseText);
alert(_14);
this.objRef.setParam("modelStatus",_14);
return;
}else{
if(null==_10.responseXML){
alert(mbGetMessage("nullXmlResponse",_10.responseText));
}
_f.doc=_10.responseXML;
_f.finishLoading();
}
}
}
}
};
this.addListener("reloadModel",this.loadModelDoc,this);
this.setModel=function(_15,_16){
_15.callListeners("newModel");
_15.doc=_16;
if((_16==null)&&_15.url){
_15.url=null;
}
_15.finishLoading();
};
this.finishLoading=function(){
if(this.doc){
if(!_SARISSA_IS_SAFARI){
this.doc.setProperty("SelectionLanguage","XPath");
if(this.namespace){
Sarissa.setXpathNamespaces(this.doc,this.namespace);
}
}
if(this.debug){
mbDebugMessage(this,"Loading Model:"+this.id+" "+(new XMLSerializer()).serializeToString(this.doc));
}
this.callListeners("loadModel");
}
};
this.newRequest=function(_17,_18){
var _19=_17;
if(_17.template){
var _1a=_17.modelNode.parentNode;
if(_SARISSA_IS_IE){
var _1b=_1a.appendChild(_1.cloneNode(true));
}else{
var _1b=_1a.appendChild(_17.modelNode.ownerDocument.importNode(_17.modelNode,true));
}
_1b.removeAttribute("id");
_1b.setAttribute("createByTemplate","true");
_19=_17.createObject(_1b);
_19.callListeners("init");
if(!_17.templates){
_17.templates=new Array();
}
_17.templates.push(_19);
}
_19.url=_18.url;
if(!_19.url){
_19.doc=null;
}
_19.method=_18.method;
_19.postData=_18.postData;
_19.loadModelDoc(_19);
};
this.deleteTemplates=function(){
if(this.templates){
var _1c;
while(_1c=this.templates.pop()){
_1c.setParam("newModel");
var _1d=this.modelNode.parentNode;
_1d.removeChild(_1c.modelNode);
}
}
};
this.saveModel=function(_1e){
if(config.serializeUrl){
var _1f=postGetLoad(config.serializeUrl,_1e.doc,"text/xml","","");
if(!_SARISSA_IS_SAFARI){
_1f.setProperty("SelectionLanguage","XPath");
Sarissa.setXpathNamespaces(_1f,"xmlns:xlink='http://www.w3.org/1999/xlink'");
}
var _20=_1f.selectSingleNode("//OnlineResource");
var _21=_20.attributes.getNamedItem("xlink:href").nodeValue;
_1e.setParam("modelSaved",_21);
}else{
alert(mbGetMessage("noSerializeUrl"));
}
};
this.createObject=function(_22){
var _23=_22.nodeName;
if(!window[_23]){
alert(mbGetMessage("errorCreatingObject",_23));
return false;
}
var _24=new window[_23](_22,this);
if(_24){
config.objects[_24.id]=_24;
return _24;
}else{
alert(mbGetMessage("errorCreatingObject",_23));
}
};
this.loadObjects=function(_25){
var _26=this.modelNode.selectNodes(_25);
for(var i=0;i<_26.length;i++){
if(_26[i].nodeName!="#text"&&_26[i].nodeName!="#comment"){
this.createObject(_26[i]);
}
}
};
this.parseConfig=function(_28){
_28.loadObjects("mb:widgets/*");
_28.loadObjects("mb:tools/*");
_28.loadObjects("mb:models/*");
};
this.refresh=function(_29){
_29.setParam("refresh");
};
this.addListener("loadModel",this.refresh,this);
this.init=function(_2a){
_2a.callListeners("init");
};
this.clearModel=function(_2b){
_2b.doc=null;
};
if(_2){
this.parentModel=_2;
this.parentModel.addListener("init",this.init,this);
this.parentModel.addListener("loadModel",this.loadModelDoc,this);
this.parentModel.addListener("newModel",this.clearModel,this);
this.parseConfig(this);
}
}
var httpStatusMsg=["uninitialized","loading","loaded","interactive","completed"];

mapbuilder.loadScript(baseDir+"/model/ModelBase.js");
function Config(_1){
this.doc=Sarissa.getDomDocument();
this.doc.async=false;
this.doc.validateOnParse=false;
if(_SARISSA_IS_SAFARI){
var _2=new XMLHttpRequest();
_2.open("GET",_1,false);
_2.send(null);
this.doc=_2.responseXML;
}else{
this.doc.load(_1);
}
if(Sarissa.getParseErrorText(this.doc)!=Sarissa.PARSED_OK){
alert("error loading config document: "+_1);
}
this.url=_1;
this.namespace="xmlns:mb='"+mbNsUrl+"'";
if(!_SARISSA_IS_SAFARI){
this.doc.setProperty("SelectionLanguage","XPath");
Sarissa.setXpathNamespaces(this.doc,this.namespace);
}
var _3=Sarissa.getDomDocument();
_3.async=false;
_3.validateOnParse=false;
if(_SARISSA_IS_SAFARI){
var _2=new XMLHttpRequest();
_2.open("GET",baseDir+"/"+mbServerConfig,false);
_2.send(null);
_3=_2.responseXML;
}else{
_3.load(baseDir+"/"+mbServerConfig);
}
if(Sarissa.getParseErrorText(_3)!=Sarissa.PARSED_OK){
}else{
if(!_SARISSA_IS_SAFARI){
_3.setProperty("SelectionLanguage","XPath");
Sarissa.setXpathNamespaces(_3,this.namespace);
}
this.proxyUrl=Mapbuilder.getProperty(_3,"/mb:MapbuilderConfig/mb:proxyUrl",this.proxyUrl);
this.serializeUrl=Mapbuilder.getProperty(_3,"/mb:MapbuilderConfig/mb:serializeUrl",this.serializeUrl);
}
_3=null;
this.loadConfigScripts=function(){
if(mapbuilder.cssToLoad){
for(var i=0;i<mapbuilder.cssToLoad.length;i++){
loadCss(mapbuilder.cssToLoad[i]);
}
mapbuilder.cssToLoad=null;
}
mapbuilder.loadScriptsFromXpath(this.doc.selectNodes("//mb:models/*"),"model/");
mapbuilder.loadScriptsFromXpath(this.doc.selectNodes("//mb:widgets/*"),"widget/");
mapbuilder.loadScriptsFromXpath(this.doc.selectNodes("//mb:tools/*"),"tool/");
var _5=this.doc.selectNodes("//mb:scriptFile");
for(var i=0;i<_5.length;i++){
scriptFile=getNodeValue(_5[i]);
mapbuilder.loadScript(scriptFile);
}
};
this.defaultLang="en";
this.lang=this.defaultLang;
if(window.cgiArgs["language"]){
this.lang=window.cgiArgs["language"];
}else{
if(window.language){
this.lang=window.language;
}
}
var _6=this.doc.documentElement;
this.skinDir=Mapbuilder.getProperty(_6,"mb:skinDir");
this.proxyUrl=Mapbuilder.getProperty(_6,"mb:proxyUrl",this.proxyUrl);
this.serializeUrl=Mapbuilder.getProperty(_6,"mb:serializeUrl",this.serializeUrl);
function loadWidgetText(_7,_8){
var _9="/widgetText.xml";
var _a;
var _b=_8+"/"+_7.lang+_9;
_a=Sarissa.getDomDocument();
_a.async=false;
_a.validateOnParse=false;
if(typeof (inlineXSL)!="undefined"){
var _c=inlineXSL[_b];
_c=_c.replace(/DOUBLE_QUOTE/g,"\"");
_a=(new DOMParser()).parseFromString(_c,"text/xml");
}else{
if(_SARISSA_IS_SAFARI){
var _d=new XMLHttpRequest();
_d.open("GET",_b,false);
_d.send(null);
_a=_d.responseXML;
}else{
try{
_a.load(_b);
}
catch(ignoredErr){
}
}
}
if(Sarissa.getParseErrorText(_a)!=Sarissa.PARSED_OK){
var _e="Error loading widgetText document: "+_b;
if(_7.lang==_7.defaultLang){
alert(_e);
}else{
alert(_e+"\nFalling back on default language=\""+_7.defaultLang+"\"");
_7.lang=_7.defaultLang;
_b=_8+"/"+_7.lang+_9;
if(_SARISSA_IS_SAFARI){
var _d=new XMLHttpRequest();
_d.open("GET",_b,false);
_d.send(null);
_a=_d.responseXML;
}else{
_a.load(_b);
}
if(Sarissa.getParseErrorText(_a)!=Sarissa.PARSED_OK){
alert("Falling back on default language failed!");
}
}
}
if(!_SARISSA_IS_SAFARI){
_a.setProperty("SelectionLanguage","XPath");
Sarissa.setXpathNamespaces(_a,_7.namespace);
}
return _a;
}
this.widgetText=loadWidgetText(this,baseDir+"/text");
userWidgetTextDir=_6.selectSingleNode("mb:userWidgetTextDir");
if(userWidgetTextDir){
var _f=loadWidgetText(this,getNodeValue(userWidgetTextDir));
if(_f){
var _10=_f.selectSingleNode("/mb:WidgetText/mb:widgets");
var _11=this.widgetText.selectSingleNode("/mb:WidgetText/mb:widgets");
if(_10&&_11){
Sarissa.copyChildNodes(_10,_11,true);
}
var _12=_f.selectSingleNode("/mb:WidgetText/mb:messages");
var _13=this.widgetText.selectSingleNode("/mb:WidgetText/mb:messages");
if(_12&&_13){
Sarissa.copyChildNodes(_12,_13,true);
}
}
}
this.objects=new Object();
ModelBase.apply(this,new Array(_6));
this.loadModel=function(_14,_15){
var _16=this.objects[_14];
if(_16&&_15){
var _17=new Object();
_17.method=_16.method;
_17.url=_15;
_16.newRequest(_16,_17);
}else{
alert(mbGetMessage("errorLoadingModel",_14,_15));
}
};
this.paintWidget=function(_18){
if(_18){
_18.paint(_18,_18.id);
}else{
alert(mbGetMessage("errorPaintingWidget"));
}
};
}
if(document.readyState==null){
mapbuilder.setLoadState(MB_LOAD_CONFIG);
config=new Config(mbConfigUrl);
config[config.id]=config;
config.loadConfigScripts();
}

mapbuilder.loadScript(baseDir+"/util/Util.js");
mapbuilder.loadScript(baseDir+"/widget/WidgetBase.js");
function ButtonBase(_1,_2){
WidgetBase.apply(this,new Array(_1,_2));
this.htmlTagId=this.getProperty("mb:buttonBar");
if(!this.htmlTagId){
this.htmlTagId=this.getProperty("mb:htmlTagId");
}
if(!this.htmlTagId){
alert(mbGetMessage("buttonBarRequired",_1.nodeName));
}
if(config.widgetText){
var _3="/mb:WidgetText/mb:widgets/mb:"+_1.nodeName;
var _4=config.widgetText.selectNodes(_3+"/*");
for(var j=0;j<_4.length;j++){
this[_4[j].nodeName]=getNodeValue(_4[j]);
}
}
this.panelHtmlTagId=this.htmlTagId+"_panel";
loadCss("controlPanel.css");
this.buttonType=this.getProperty("mb:class");
this.buttonType=this.buttonType?this.buttonType.toUpperCase():null;
if(this.buttonType=="RADIOBUTTON"){
this.enabled=false;
}
this.olButtonType={"RADIOBUTTON":OpenLayers.Control.TYPE_TOOL,"TOOL":OpenLayers.Control.TYPE_TOOL,"BUTTON":OpenLayers.Control.TYPE_BUTTON,"TOGGLE":OpenLayers.Control.TYPE_TOGGLE};
this.action=this.getProperty("mb:action");
var _6=this.getProperty("mb:tooltip");
if(_6){
this.tooltip=_6;
}
var _7=this.getProperty("mb:disabledSrc");
if(_7){
this.disabledImage=config.skinDir+_7;
}
var _8=this.getProperty("mb:enabledSrc");
if(_8){
this.enabledImage=config.skinDir+_8;
}
this.cursor="default";
this.cursor=this.getProperty("mb:cursor");
this.selected=Mapbuilder.parseBoolean(this.getProperty("mb:selected",false));
this.getButtonClass=function(_9,_a){
var _b;
if(_9.control.displayClass){
_b=_9.control.displayClass;
}else{
_b=_9.control.CLASS_NAME;
_b=_b.replace(/OpenLayers/,"ol").replace(/\./g,"");
}
_b+="Item";
return "."+_b+_a;
};
this.control=null;
this.doAction=function(){
};
this.select=function(){
if(this.control.type==OpenLayers.Control.TYPE_BUTTON){
this.control.trigger();
}else{
this.panel.activateControl(this.control);
}
};
this.doSelect=function(_c,_d){
};
this.attachToOL=function(_e,_f){
if(_e.control){
return;
}
if(_f&&(_f!=_e.id)){
return;
}
if(!_e.createControl){
return;
}
var _10=_e.createControl(_e);
var _11=_e.olButtonType[_e.buttonType]||_10.prototype.type;
var _12=OpenLayers.Class(_10,{objRef:_e,type:_11,superclass:_10.prototype,trigger:function(){
if(this.superclass.trigger){
this.superclass.trigger.call(this);
}
_e.doSelect(_e,true);
},activate:function(){
if(this.superclass.activate.call(this)){
this.panel_div.style.backgroundImage="url(\""+_e.enabledImage+"\")";
this.map.div.style.cursor=_e.cursor;
this.map.mbCursor=_e.cursor;
_e.enabled=true;
this.active=true;
_e.doSelect(_e,true);
}
},deactivate:function(){
if(this.superclass.deactivate.call(this)){
this.panel_div.style.backgroundImage="url(\""+_e.disabledImage+"\")";
_e.enabled=false;
this.active=false;
if(map.getControlsBy("active",true).length==0){
this.map.div.style.cursor="";
this.map.mbCursor="";
}
_e.doSelect(_e,false);
}
},destroy:function(){
try{
this.superclass.destroy.apply(this,arguments);
}
catch(e){
OpenLayers.Control.prototype.destroy.apply(this,arguments);
}
this.superclass=null;
OpenLayers.Event.stopObservingElement(this.panel_div);
this.objRef.panel.div.removeChild(this.panel_div);
this.objRef.control=null;
this.objRef=null;
this.panel_div=null;
this.div=null;
}});
if(!_e.control){
_e.control=_e.instantiateControl?_e.instantiateControl(_e,_12):new _12();
}
var map=_e.targetContext.map;
_e.panel=_e.targetContext.buttonBars[_e.htmlTagId];
if(!_e.panel||_e.panel.map==null){
if(!document.getElementById(_e.panelHtmlTagId)){
var _14=document.createElement("div");
_14.setAttribute("id",_e.panelHtmlTagId);
_14.setAttribute("class","olControlPanel");
var _15=_e.getNode();
_15.appendChild(_14);
_15.innerHTML+=" ";
}
var _16=OpenLayers.Class(OpenLayers.Control.Panel,{div:document.getElementById(_e.panelHtmlTagId),defaultControl:null,destroy:function(){
_15.removeChild(this.div);
OpenLayers.Control.prototype.destroy.apply(this,arguments);
this.div=null;
_e.panel=null;
}});
_e.panel=new _16();
_e.targetContext.buttonBars[_e.htmlTagId]=_e.panel;
map.addControl(_e.panel);
}
if(OpenLayers.Util.indexOf(_e.control,_e.panel.controls)==-1){
var _17=OpenLayers.Event.stop;
OpenLayers.Event.stop=function(){
};
_e.panel.addControls(_e.control);
OpenLayers.Event.stop=_17;
}
if(_e.tooltip){
_e.control.panel_div.title=_e.tooltip;
}
_e.control.panel_div.style.backgroundImage="url(\""+_e.disabledImage+"\")";
if(_e.selected==true){
_e.control.activate();
}
};
this.buttonInit=function(_18){
var _19=_18.widgetNode.selectSingleNode("mb:targetContext");
if(_19){
_18.targetContext=window.config.objects[getNodeValue(_19)];
if(!_18.targetModel){
alert(mbGetMessage("noTargetContext",getNodeValue(_19),_18.id));
}
}else{
_18.targetContext=_18.targetModel;
}
if(!_18.targetContext.buttonBars){
_18.targetContext.buttonBars=new Array();
}
_18.targetContext.addListener("refresh",_18.attachToOL,_18);
};
this.model.addListener("init",this.buttonInit,this);
this.model.removeListener("newNodel",this.clearWidget,this);
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function Button(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.cursor="default";
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{CLASS_NAME:"mbControl."+_3.id,type:(_3.buttonType=="RadioButton")?OpenLayers.Control.TYPE_TOOL:OpenLayers.Control.TYPE_BUTTON,trigger:function(){
eval("config.objects."+_3.action);
},activate:function(){
eval("config.objects."+_3.action);
this.active=true;
return true;
}});
return _4;
};
this.instantiateControl=function(_5,_6){
return new _6();
};
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function Back(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{type:OpenLayers.Control.TYPE_BUTTON,trigger:function(){
var _5=this.objRef;
_5.targetModel.setParam("historyBack");
var _6=_5.targetModel.previousExtent;
if(_6){
_5.targetModel.setParam("historyStop");
this.map.setCenter(_6.center);
this.map.zoomToScale(_6.scale);
_5.targetModel.setParam("historyStart");
}
},CLASS_NAME:"mbControl.Back"});
return _4;
};
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function Forward(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{type:OpenLayers.Control.TYPE_BUTTON,trigger:function(){
var _5=this.objRef;
_5.targetModel.setParam("historyForward");
var _6=_5.targetModel.nextExtent;
if(_6){
_5.targetModel.setParam("historyStop");
this.map.setCenter(_6.center);
this.map.zoomToScale(_6.scale);
_5.targetModel.setParam("historyStart");
}
},CLASS_NAME:"mbControl.Forward"});
return _4;
};
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
function ZoomIn(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.cursor="crosshair";
this.createControl=function(){
return OpenLayers.Control.ZoomBox;
};
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
function ZoomOut(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.cursor="crosshair";
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{CLASS_NAME:"mbControl.ZoomOut",type:OpenLayers.Control.TYPE_TOOL,draw:function(){
this.handler=new OpenLayers.Handler.Box(this,{done:this.zoomBox},{keyMask:this.keyMask});
},zoomBox:function(_5){
if(_5 instanceof OpenLayers.Bounds){
var _6=new OpenLayers.Pixel(_5.left,_5.bottom);
var _7=new OpenLayers.Pixel(_5.right,_5.top);
var _8=new OpenLayers.Bounds(_6.x,_6.y,_7.x,_7.y);
var _9=(this.map.getSize().w+this.map.getSize().h)/2;
var _a=(Math.abs(_8.getWidth())+Math.abs(_8.getHeight()))/2;
var _b=this.map.getScale()*(_9/_a);
this.map.setCenter(_8.getCenterLonLat());
this.map.zoomToScale(_b);
}else{
this.map.setCenter(this.map.getLonLatFromPixel(_5),parseInt(this.map.getZoom()-1));
}
}});
return _4;
};
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function DragPan(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.createControl=function(_3){
return OpenLayers.Control.DragPan;
};
this.cursor="move";
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
function Reset(_1,_2){
this.createControl=function(){
return OpenLayers.Control.ZoomToMaxExtent;
};
ButtonBase.apply(this,new Array(_1,_2));
}

loadCss("openlayers/style.css");
mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
mapbuilder.loadScript(baseDir+"/util/Util.js");
mapbuilder.loadScript(baseDir+"/widget/WidgetBase.js");
mapbuilder.loadScript(baseDir+"/tool/Extent.js");
function MapPaneOL(_1,_2){
WidgetBase.apply(this,new Array(_1,_2));
OpenLayers.ImgPath=config.skinDir+"/images/openlayers/";
OpenLayers.ProxyHost=config.proxyUrl+"/?url=";
this.containerNodeId=this.htmlTagId;
_2.containerModel=this.model;
if(!this.model.extent){
this.model.extent=new Extent(this.model);
this.model.addFirstListener("loadModel",this.model.extent.firstInit,this.model.extent);
}
this.tileGutter=this.getProperty("mb:tileGutter",0);
this.tileBuffer=parseInt(this.getProperty("mb:tileBuffer",2));
this.singleTile=Mapbuilder.parseBoolean(this.getProperty("mb:singleTile",true));
this.tileSize=parseInt(this.getProperty("mb:tileSize",256));
this.imageReproject=Mapbuilder.parseBoolean(this.getProperty("mb:imageReproject",false));
this.imageBuffer=parseInt(this.getProperty("mb:imageBuffer",2));
this.displayOutsideMaxExtent=Mapbuilder.parseBoolean(this.getProperty("mb:displayOutsideMaxExtent",false));
this.loadingLayers=0;
this.refreshWmsLayers=function(_3){
var _4=_3.model.getParam("refreshWmsLayers");
var _5=(new Date()).getTime();
var _6=_4?[_3.getLayer(_3,_4)]:_3.model.map.layers;
for(var i in _6){
if(_6[i].CLASS_NAME.indexOf("OpenLayers.Layer.WMS")==0){
_6[i].mergeNewParams({uniqueId:_5});
}
}
};
this.model.addListener("refreshWmsLayers",this.refreshWmsLayers,this);
this.model.addListener("refresh",this.paint,this);
this.model.addFirstListener("newModel",this.clear,this);
this.model.addListener("hidden",this.hidden,this);
this.model.addListener("addLayer",this.addLayer,this);
this.model.addListener("deleteLayer",this.deleteLayer,this);
this.model.addListener("moveLayerUp",this.moveLayerUp,this);
this.model.addListener("moveLayerDown",this.moveLayerDown,this);
this.model.addListener("opacity",this.setOpacity,this);
this.model.addListener("bbox",this.zoomToBbox,this);
}
MapPaneOL.prototype.paint=function(_8,_9){
if(_8.model.doc.selectSingleNode("//wmc:OWSContext")){
_8.context="OWS";
}else{
if(_8.model.doc.selectSingleNode("//wmc:ViewContext")){
_8.context="View";
}else{
alert(mbGetMessage("noContextDefined"));
}
}
var _a=_8.model.proj;
var _b=null;
_b=_8.getProperty("mb:maxExtent");
_b=(_b)?_b.split(" "):null;
if(!_b){
_b=_8.model.getBoundingBox();
}
_b=(_b)?new OpenLayers.Bounds(_b[0],_b[1],_b[2],_b[3]):null;
if(_b==null){
alert(mbGetMessage("noBboxInContext"));
}
var _c=null;
_c=_8.getProperty("mb:maxResolution");
_c=(_c)?parseFloat(_c):"auto";
var _d=null;
_d=_8.getProperty("mb:minResolution");
_d=(_d)?parseFloat(_d):undefined;
_8.transitionEffect=_8.getProperty("mb:transitionEffect",undefined);
var _e=_a.getUnits()=="meters"?"m":_a.getUnits();
var _f=_8.getProperty("mb:resolutions");
_f=_f?_f.split(","):null;
if(_f){
for(var r=0;r<_f.length;r++){
_f[r]=parseFloat(_f[r]);
}
}
var _11=_8.getProperty("mb:scales");
if(_11){
_11=_11.split(",");
_f=new Array();
for(var s=0;s<_11.length;s++){
_f.push(OpenLayers.Util.getResolutionFromScale(_11[s],_e));
}
}
if(_f){
_8.model.extent.setZoomLevels(true,_f);
}else{
_8.model.extent.setZoomLevels(false);
}
var _13=document.getElementById(_8.containerNodeId);
var _14=Mapbuilder.parseBoolean(_8.getProperty("mb:fixedSize",false));
if(_14){
_13.style.width=_8.model.getWindowWidth()+"px";
_13.style.height=_8.model.getWindowHeight()+"px";
}
var _15={controls:[],projection:_a,units:_e,fractionalZoom:true,maxExtent:_b,maxResolution:_c,minResolution:_d,resolutions:_f,theme:null,destroy:function(_16){
if(_16!=true){
this.mbMapPane.model.setParam("newModel",true);
}else{
this.mbMapPane=null;
this.mbCursor=null;
OpenLayers.Map.prototype.destroy.apply(this,arguments);
this.layerContainerDiv=null;
this.baseLayer=null;
}
}};
if(!_8.model.map){
_8.model.map=new OpenLayers.Map(_13,_15);
_8.model.map.Z_INDEX_BASE.Control=10000;
var _17=null;
if(_8.context=="OWS"&&_8.model.getBaseLayer()){
var _18=_8.model.getBaseLayer();
var _19=_18.selectSingleNode("ows:TileSet/ows:SRS");
if(_19){
_8.model.setSRS(getNodeValue(_19));
}
_e=_a.getUnits()=="meters"?"m":_a.getUnits();
var _1a=_18.selectSingleNode("ows:TileSet/ows:BoundingBox");
if(_1a){
_b=new OpenLayers.Bounds(_1a.selectSingleNode("@minx").nodeValue,_1a.selectSingleNode("@miny").nodeValue,_1a.selectSingleNode("@maxx").nodeValue,_1a.selectSingleNode("@maxy").nodeValue);
}
var _f=_18.selectSingleNode("ows:TileSet/ows:Resolutions");
_f=_f?getNodeValue(_f).split(","):null;
if(_f){
for(var r=0;r<_f.length;r++){
_f[r]=parseFloat(_f[r]);
}
}
var _1b=_18.selectSingleNode("ows:TileSet/ows:Width");
if(_1b){
_8.tileSize=parseInt(_1b.nodeValue);
}
var _1c=_18.selectSingleNode("ows:TileSet/ows:Format");
if(_1c){
_1c=_1c.nodeValue;
}
var _1d=_18.selectSingleNode("wmc:Server/@service");
_1d=(_1d)?_1d.nodeValue:"";
var _1e=Mapbuilder.getProperty(_18,"wmc:Title","");
var _1f=Mapbuilder.getProperty(_18,"wmc:Name","");
var _20=Mapbuilder.getProperty(_18,"ows:TileSet/ows:Layers","hybrid");
var _21=Mapbuilder.getProperty(_18,"wmc:Server/wmc:OnlineResource/@xlink:href","");
var _22={units:_e,projection:_a,maxExtent:_b,alpha:false,isBaseLayer:true,displayOutsideMaxExtent:_8.displayOutsideMaxExtent,ratio:1};
switch(_1d){
case "OGC":
case "WMS":
case "wms":
case "OGC:WMS":
_22.ratio=_8.imageBuffer;
var _23=new Array();
_17=new OpenLayers.Layer.WMS(_1e,_21,{layers:_1f,format:_1c},_22);
break;
case "WMS-C":
case "OGC:WMS-C":
_22.gutter=_8.tileGutter;
_22.buffer=_8.tileBuffer;
_22.tileSize=new OpenLayers.Size(_8.tileSize,_8.tileSize);
_17=new OpenLayers.Layer.WMS(_1e,_21,{layers:_1f,format:_1c},_22);
_8.model.map.fractionalZoom=false;
break;
case "GMAP":
case "Google":
if(_b){
_22.maxExtent=_b;
}
var _24=(_8.model.getSRS()=="EPSG:900913")?true:false;
switch(_20){
case "aerial":
case "satellite":
_20=G_SATELLITE_MAP;
break;
case "road":
case "normal":
_20=G_NORMAL_MAP;
break;
default:
_20=G_HYBRID_MAP;
}
_17=new OpenLayers.Layer.Google(_1f,{type:_20,minZoomLevel:0,maxZoomLevel:19,sphericalMercator:_24,maxResolution:156543.0339},_22);
_8.model.map.numZoomLevels=20;
_8.model.map.fractionalZoom=false;
break;
case "YMAP":
case "Yahoo":
if(_b){
_22.maxExtent=_b;
}
var _24=(_8.model.getSRS()=="EPSG:900913")?true:false;
_17=new OpenLayers.Layer.Yahoo(_1f,{maxZoomLevel:21,sphericalMercator:_24},_22);
_8.model.map.fractionalZoom=false;
break;
case "VE":
case "Microsoft":
if(_b){
_22.maxExtent=_b;
}
var _24=(_8.model.getSRS()=="EPSG:900913")?true:false;
switch(_20){
case "aerial":
case "satellite":
_20=VEMapStyle.Aerial;
break;
case "road":
case "normal":
_20=VEMapStyle.Road;
break;
default:
_20=VEMapStyle.Hybrid;
}
_17=new OpenLayers.Layer.VirtualEarth(_1f,{minZoomLevel:0,maxZoomLevel:21,type:_20});
_8.model.map.fractionalZoom=false;
break;
case "MultiMap":
if(_b){
_22.maxExtent=_b;
}
var _24=(_8.model.getSRS()=="EPSG:900913")?true:false;
_17=new OpenLayers.Layer.MultiMap(_1f,{maxZoomLevel:21,sphericalMercator:_24},_22);
_8.model.map.fractionalZoom=false;
break;
default:
alert(mbGetMessage("layerTypeNotSupported",_1d));
}
}else{
var _22={units:_e,projection:_a.srsCode,maxExtent:_b,maxResolution:_c,minResolution:_d,resolutions:_f,alpha:false,isBaseLayer:true,displayOutsideMaxExtent:_8.displayOutsideMaxExtent,ratio:1,singleTile:true,transitionEffect:_8.transitionEffect,visibility:false,moveTo:function(){
return true;
}};
_17=new OpenLayers.Layer.WMS("baselayer",config.skinDir+"/images/openlayers/blank.gif",null,_22);
}
_8.model.map.addLayer(_17);
}else{
_8.deleteAllLayers(_8);
}
var _25=_8.model.getAllLayers();
if(!_8.oLlayers){
_8.oLlayers={};
}
for(var i=0;i<=_25.length-1;i++){
_8.addLayer(_8,_25[i]);
}
var _27=_8.model.getBoundingBox();
_8.model.map.mbMapPane=_8;
_8.model.map.events.register("moveend",_8.model.map,_8.updateContext);
_8.model.map.events.register("mouseup",_8.model.map,_8.updateMouse);
_8.model.callListeners("bbox");
};
MapPaneOL.prototype.clear=function(_28){
if(_28.model.map){
OpenLayers.Event.stopObservingElement(window);
_28.model.map.destroy(true);
_28.model.map=null;
_28.oLlayers={};
}
};
MapPaneOL.prototype.increaseLoadingLayers=function(e){
++this.loadingLayers;
var _2a=mbGetMessage((this.loadingLayers>1)?"loadingLayers":"loadingLayer",this.loadingLayers);
this.model.setParam("modelStatus",_2a);
};
MapPaneOL.prototype.decreaseLoadingLayers=function(e){
--this.loadingLayers;
var _2c=this.loadingLayers>0?mbGetMessage((this.loadingLayers>1)?"loadingLayers":"loadingLayer",this.loadingLayers):null;
this.model.setParam("modelStatus",_2c);
};
MapPaneOL.prototype.updateContext=function(e){
var _2e=e.object.mbMapPane;
var _2f=_2e.model.map.getExtent().toBBOX().split(",");
var ul=new Array(_2f[0],_2f[3]);
var lr=new Array(_2f[2],_2f[1]);
if(_2e.model.getWindowWidth()!=e.element.offsetWidth){
_2e.model.setWindowWidth(e.element.offsetWidth);
}
if(_2e.model.getWindowHeight()!=e.element.offsetHeight){
_2e.model.setWindowHeight(e.element.offsetHeight);
}
var _32=_2e.model.getParam("aoi");
var _33=new Array(ul,lr);
if(!_32||_32.toString()!=_33.toString()){
_2e.model.setBoundingBox(new Array(ul[0],lr[1],lr[0],ul[1]));
_2e.model.extent.setSize(_2e.model.map.getResolution());
_2e.model.setParam("aoi",_33);
}
};
MapPaneOL.prototype.updateMouse=function(e){
var _35=e.object.mbMapPane;
if(_35.model.map.mbCursor){
_35.model.map.div.style.cursor=_35.model.map.mbCursor;
}
};
MapPaneOL.prototype.zoomToBbox=function(_36){
if(_36.model.map){
var _37=_36.model.getBoundingBox();
var _38=[];
var _39=_36.model.map.getExtent();
if(_39){
_38=_39.toBBOX();
}
if(_37.toString()!=_38.toString()){
_36.model.map.zoomToExtent(new OpenLayers.Bounds(_37[0],_37[1],_37[2],_37[3]));
}
}
};
MapPaneOL.prototype.hidden=function(_3a,_3b){
var vis=_3a.model.getHidden(_3b);
if(vis=="1"){
var _3d=false;
}else{
var _3d=true;
}
var _3e=_3a.getLayer(_3a,_3b);
if(_3e){
_3e.setVisibility(_3d);
}
};
MapPaneOL.prototype.getLayer=function(_3f,_40){
if(!_3f.oLlayers[_40]){
_40=_3f.model.getLayerIdByName(_40)||_40;
}
if(_3f.oLlayers[_40]&&_3f.oLlayers[_40].id){
return _3f.model.map.getLayer(_3f.oLlayers[_40].id);
}else{
return false;
}
};
MapPaneOL.prototype.deleteLayer=function(_41,_42){
if(_41.oLlayers[_42]){
_41.model.map.removeLayer(_41.oLlayers[_42]);
}
};
MapPaneOL.prototype.deleteAllLayers=function(_43){
for(var i in _43.oLlayers){
var _45=_43.oLlayers[i];
_45.destroy();
}
_43.oLlayers={};
};
MapPaneOL.prototype.moveLayerUp=function(_46,_47){
var map=_46.model.map;
map.raiseLayer(map.getLayer(_46.oLlayers[_47].id),1);
};
MapPaneOL.prototype.moveLayerDown=function(_49,_4a){
_49.model.map.raiseLayer(_49.getLayer(_49,_4a),-1);
};
MapPaneOL.prototype.setOpacity=function(_4b,_4c){
var _4d="1";
_4d=_4b.model.getOpacity(_4c);
_4b.getLayer(_4b,_4c).setOpacity(_4d);
};
MapPaneOL.prototype.addLayer=function(_4e,_4f){
var _50=_4f;
var _51=_50.selectSingleNode("wmc:Server/@service");
_51=(_51)?_51.nodeValue:"";
var _52=Mapbuilder.getProperty(_50,"wmc:Title","");
layerName=Mapbuilder.getProperty(_50,"wmc:Name","");
var _53;
if(_50.selectSingleNode("@id")&&_50.selectSingleNode("@id").nodeValue){
_53=_50.selectSingleNode("@id").nodeValue;
}else{
_53=layerName;
}
if(_4e.context=="OWS"){
var _54=Mapbuilder.getProperty(_50,"wmc:Server/wmc:OnlineResource/@xlink:href","");
}else{
if(_SARISSA_IS_SAFARI){
var _55=_50.selectSingleNode("wmc:Server/wmc:OnlineResource");
var _54=_55.attributes[1].nodeValue;
}else{
if(_SARISSA_IS_OPERA){
var _54=_50.selectSingleNode("wmc:Server/wmc:OnlineResource").getAttributeNS("http://www.w3.org/1999/xlink","href");
}else{
var _54=_50.selectSingleNode("wmc:Server/wmc:OnlineResource").getAttribute("xlink:href");
}
}
}
var _56=_50.selectSingleNode("wmc:FormatList/wmc:Format[@current='1']");
if(!_56){
_56=_50.selectSingleNode("wmc:FormatList/wmc:Format");
}
_56=_56?getNodeValue(_56):"image/gif";
var vis=_50.selectSingleNode("@hidden");
if(vis){
if(vis.nodeValue=="1"){
vis=false;
}else{
vis=true;
}
}
var _58=_50.selectSingleNode("@queryable");
if(_58){
if(_58.nodeValue=="1"){
_58=true;
}else{
_58=false;
}
}
var _59=_50.selectSingleNode("@opacity");
if(_59){
_59=_59.nodeValue;
}else{
_59=false;
}
var _5a=_50.selectSingleNode("wmc:StyleList/wmc:Style[@current=1]");
_4e.IE6=false;
var _5b={visibility:vis,projection:_4e.model.map.baseLayer.projection,queryable:_58,maxExtent:_4e.model.map.baseLayer.maxExtent,maxResolution:_4e.model.map.baseLayer.maxResolution,minResolution:_4e.model.map.baseLayer.minResolution,alpha:_56.indexOf("png")!=-1?_4e.IE6:false,isBaseLayer:false,displayOutsideMaxExtent:_4e.displayOutsideMaxExtent};
switch(_51){
case "OGC":
case "WMS":
case "wms":
case "OGC:WMS":
if(!_4e.model.map.baseLayer){
_5b.isBaseLayer=true;
}else{
_5b.reproject=_4e.imageReproject;
_5b.isBaseLayer=false;
}
_5b.ratio=_4e.imageBuffer;
_5b.singleTile=_4e.singleTile;
if(_4e.singleTile==true){
_5b.transitionEffect=_4e.transitionEffect;
}
var _5c=new Array();
_5c=sld2UrlParam(_5a);
if(_4e.model.timestampList&&_4e.model.timestampList.getAttribute("layerId")==_53){
var ts=_4e.model.timestampList.childNodes[0];
_4e.oLlayers[_53]=new OpenLayers.Layer.WMS(_52,_54,{layers:layerName,transparent:_56.indexOf("jpeg")==-1?"TRUE":"FALSE","TIME":getNodeValue(ts),format:_56,sld:_5c.sld,sld_body:_5c.sld_body,styles:_5c.styles},_5b);
this.model.addListener("timestamp",this.timestampListener,this);
}else{
_4e.oLlayers[_53]=new OpenLayers.Layer.WMS(_52,_54,{layers:layerName,transparent:_56.indexOf("jpeg")==-1?"TRUE":"FALSE",format:_56,sld:_5c.sld,sld_body:_5c.sld_body,styles:_5c.styles},_5b);
}
break;
case "WMS-C":
case "OGC:WMS-C":
if(!_4e.model.map.baseLayer){
_5b.isBaseLayer=true;
}else{
_5b.reproject=_4e.imageReproject;
_5b.isBaseLayer=false;
}
_5b.gutter=_4e.tileGutter;
_5b.buffer=_4e.tileBuffer;
_5b.tileSize=new OpenLayers.Size(_4e.tileSize,_4e.tileSize);
var _5c=new Array();
_5c=sld2UrlParam(_5a);
_4e.oLlayers[_53]=new OpenLayers.Layer.WMS(_52,_54,{layers:layerName,transparent:_56.indexOf("jpeg")==-1?"TRUE":"FALSE",format:_56,sld:_5c.sld,sld_body:_5c.sld_body,styles:_5c.styles},_5b);
_4e.model.map.fractionalZoom=false;
break;
case "WFS":
case "wfs":
case "OGC:WFS":
style=sld2OlStyle(_5a);
if(style){
_5b.styleMap=new OpenLayers.StyleMap(style);
}else{
_5b.style=_4e.getWebSafeStyle(_4e,2*i+1);
}
_5b.featureClass=OpenLayers.Feature.WFS;
_4e.oLlayers[_53]=new OpenLayers.Layer.WFS(_52,_54,{typename:_53,maxfeatures:1000},_5b);
break;
case "gml":
case "OGC:GML":
style=sld2OlStyle(_5a);
if(style){
_5b.styleMap=new OpenLayers.StyleMap(style);
}else{
_5b.style=_4e.getWebSafeStyle(_4e,2*i+1);
}
_4e.oLlayers[_53]=new OpenLayers.Layer.GML(_52,_54,_5b);
break;
case "KML":
case "kml":
_4e.oLlayers[_53]=new OpenLayers.Layer.GML(_52,_54,{format:OpenLayers.Format.KML});
break;
default:
alert(mbGetMessage("layerTypeNotSupported",_51));
}
if(_59&&_4e.oLlayers[_53]){
_4e.oLlayers[_53].setOpacity(_59);
}
_4e.oLlayers[_53].events.register("loadstart",_4e,_4e.increaseLoadingLayers);
_4e.oLlayers[_53].events.register("loadend",_4e,_4e.decreaseLoadingLayers);
_4e.model.map.addLayer(_4e.oLlayers[_53]);
};
MapPaneOL.prototype.getWebSafeStyle=function(_5e,_5f){
colors=new Array("00","33","66","99","CC","FF");
_5f=(_5f)?_5f:0;
_5f=(_5f<0)?0:_5f;
_5f=(_5f>215)?215:_5f;
i=parseInt(_5f/36);
j=parseInt((_5f-i*36)/6);
k=parseInt((_5f-i*36-j*6));
var _60="#"+colors[i]+colors[j]+colors[k];
var _61={fillColor:"#808080",fillOpacity:1,strokeColor:"#000000",strokeOpacity:1,strokeWidth:1,pointRadius:6};
var _62=OpenLayers.Util.extend(_61,OpenLayers.Feature.Vector.style["default"]);
_62.fillColor=_60;
_62.strokeColor=_60;
_62.map=_5e.model.map;
return _62;
};
MapPaneOL.prototype.refreshLayer=function(_63,_64,_65){
_65["version"]=Math.random();
_63.getLayer(_63,_64).mergeNewParams(_65);
};
MapPaneOL.prototype.timestampListener=function(_66,_67){
if(window.movieLoop.frameIsLoading==true){
return;
}
var _68=_66.model.timestampList.getAttribute("layerId");
var ts=_66.model.timestampList.childNodes[_67];
if(_68&&ts){
var _6a=_66.oLlayers[_68];
if(!_6a.grid){
return;
}
div=_6a.grid[0][0].imgDiv;
var _6b=div.src||div.firstChild.src;
var _6c=_6b.replace(/TIME\=.*?\&/,"TIME="+getNodeValue(ts)+"&");
if(_6b==_6c){
return;
}
function imageLoaded(){
window.movieLoop.frameIsLoading=false;
if(element.removeEventListener){
element.removeEventListener("load",imageLoaded,false);
}else{
if(element.detachEvent){
element.detachEvent("onload",imageLoaded);
}
}
}
window.movieLoop.frameIsLoading=true;
var _6d=div.nodeName.toUpperCase()=="IMG"?div:div.firstChild;
if(_6d.addEventListener){
_6d.addEventListener("load",imageLoaded,false);
}else{
if(_6d.attachEvent){
_6d.attachEvent("onload",imageLoaded);
}
}
if(_66.IE6){
OpenLayers.Util.modifyAlphaImageDiv(div,null,null,null,_6c);
}else{
_6d.src=_6c;
}
}
};

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function CursorTrack(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.showPx=Mapbuilder.parseBoolean(this.getProperty("mb:showPx",false));
this.showXY=Mapbuilder.parseBoolean(this.getProperty("mb:showXY",false));
this.showLatLong=Mapbuilder.parseBoolean(this.getProperty("mb:showLatLong",true));
this.showDMS=Mapbuilder.parseBoolean(this.getProperty("mb:showDMS",false));
this.showDM=Mapbuilder.parseBoolean(this.getProperty("mb:showDM",false));
this.showMGRS=Mapbuilder.parseBoolean(this.getProperty("mb:showMGRS",false));
if(this.showMGRS){
mapbuilder.loadScript(baseDir+"/util/MGRS.js");
}
this.precision=this.getProperty("mb:precision",2);
this.formName="CursorTrackForm_"+mbIds.getId();
this.stylesheet.setParameter("formName",this.formName);
this.init=function(_3){
_3.proj=new OpenLayers.Projection(_3.model.getSRS());
_3.model.map.events.register("mousemove",_3,_3.mousemoveHandler);
_3.model.map.events.register("mouseout",_3,_3.mouseoutHandler);
if(this.showMGRS){
this.MGRS=new MGRS();
}
};
this.model.addListener("loadModel",this.init,this);
this.clear=function(_4){
if(_4.model.map&&_4.model.map.events){
_4.model.map.events.unregister("mousemove",_4,_4.mousemoveHandler);
_4.model.map.events.unregister("mouseout",_4,_4.mouseoutHandler);
}
};
this.model.addListener("newModel",this.clear,this);
this.mousemoveHandler=function(_5){
var _6=document.getElementById(this.formName);
if(!_5){
return;
}
var _7=this.model.map.getLonLatFromPixel(_5.xy);
var pt=new OpenLayers.Geometry.Point(_7.lon,_7.lat);
pt.transform(this.proj,new OpenLayers.Projection("EPSG:4326"));
var _9=new OpenLayers.LonLat(pt.x,pt.y);
if(this.showPx){
if(_6.px){
_6.px.value=_5.xy.x;
}
if(_6.py){
_6.py.value=_5.xy.y;
}
}
if(this.showXY){
if(_6.x){
_6.x.value=_7.lon.toFixed(this.precision);
}
if(_6.y){
_6.y.value=_7.lat.toFixed(this.precision);
}
}
if(this.showLatLong){
if(_6.longitude){
_6.longitude.value=_9.lon.toFixed(this.precision);
}
if(_6.latitude){
_6.latitude.value=_9.lat.toFixed(this.precision);
}
}
if(this.showDMS){
var _a=this.convertDMS(_9.lon,"LON");
if(_6.longdeg){
_6.longdeg.value=_a[0];
}
if(_6.longmin){
_6.longmin.value=_a[1];
}
if(_6.longsec){
_6.longsec.value=_a[2];
}
if(_6.longH){
_6.longH.value=_a[3];
}
var _b=this.convertDMS(_9.lat,"LAT");
if(_6.latdeg){
_6.latdeg.value=_b[0];
}
if(_6.latmin){
_6.latmin.value=_b[1];
}
if(_6.latsec){
_6.latsec.value=_b[2];
}
if(_6.latH){
_6.latH.value=_b[3];
}
}
if(this.showDM){
var _a=this.convertDM(_9.lon,"LON");
if(_6.longDMdeg){
_6.longDMdeg.value=_a[0];
}
if(_6.longDMmin){
_6.longDMmin.value=_a[1];
}
if(_6.longDMH){
_6.longDMH.value=_a[2];
}
var _b=this.convertDM(_9.lat,"LAT");
if(_6.latDMdeg){
_6.latDMdeg.value=_b[0];
}
if(_6.latDMmin){
_6.latDMmin.value=_b[1];
}
if(_6.latDMH){
_6.latDMH.value=_b[2];
}
}
if(this.showMGRS){
if(!this.MGRS){
this.MGRS=new MGRS();
}
_6.mgrs.value=this.MGRS.convert(_9.lat,_9.lon);
}
};
this.mouseoutHandler=function(_c){
var _d=document.getElementById(this.formName);
if(this.showPx){
if(_d.px){
_d.px.value="";
}
if(_d.py){
_d.py.value="";
}
}
if(this.showXY){
if(_d.x){
_d.x.value="";
}
if(_d.y){
_d.y.value="";
}
}
if(this.showLatLong){
if(_d.longitude){
_d.longitude.value="";
}
if(_d.latitude){
_d.latitude.value="";
}
}
if(this.showDMS){
if(_d.longdeg){
_d.longdeg.value="";
}
if(_d.longmin){
_d.longmin.value="";
}
if(_d.longsec){
_d.longsec.value="";
}
if(_d.longH){
_d.longH.value="";
}
if(_d.latdeg){
_d.latdeg.value="";
}
if(_d.latmin){
_d.latmin.value="";
}
if(_d.latsec){
_d.latsec.value="";
}
if(_d.latH){
_d.latH.value="";
}
}
if(this.showDM){
if(_d.longDMdeg){
_d.longDMdeg.value="";
}
if(_d.longDMmin){
_d.longDMmin.value="";
}
if(_d.longDMH){
_d.longDMH.value="";
}
if(_d.latDMdeg){
_d.latDMdeg.value="";
}
if(_d.latDMmin){
_d.latDMmin.value="";
}
if(_d.latDMH){
_d.latDMH.value="";
}
}
if(this.showMGRS){
if(_d.mgrs){
_d.mgrs.value="";
}
}
};
this.convertDMS=function(_e,_f){
var _10=new Array();
abscoordinate=Math.abs(_e);
coordinatedegrees=Math.floor(abscoordinate);
coordinateminutes=(abscoordinate-coordinatedegrees)/(1/60);
tempcoordinateminutes=coordinateminutes;
coordinateminutes=Math.floor(coordinateminutes);
coordinateseconds=(tempcoordinateminutes-coordinateminutes)/(1/60);
coordinateseconds=Math.round(coordinateseconds*10);
coordinateseconds/=10;
if(coordinatedegrees<10){
coordinatedegrees="0"+coordinatedegrees;
}
if(coordinateminutes<10){
coordinateminutes="0"+coordinateminutes;
}
if(coordinateseconds<10){
coordinateseconds="0"+coordinateseconds;
}
_10[0]=coordinatedegrees;
_10[1]=coordinateminutes;
_10[2]=coordinateseconds;
_10[3]=this.getHemi(_e,_f);
return _10;
};
this.convertDM=function(_11,_12){
var _13=new Array();
abscoordinate=Math.abs(_11);
coordinatedegrees=Math.floor(abscoordinate);
coordinateminutes=(abscoordinate-coordinatedegrees)*60;
coordinateminutes=Math.round(coordinateminutes*1000);
coordinateminutes/=1000;
if(coordinatedegrees<10){
coordinatedegrees="0"+coordinatedegrees;
}
if(coordinateminutes<10){
coordinateminutes="0"+coordinateminutes;
}
_13[0]=coordinatedegrees;
_13[1]=coordinateminutes;
_13[2]=this.getHemi(_11,_12);
return _13;
};
this.getHemi=function(_14,_15){
var _16="";
if(_15=="LAT"){
if(_14>=0){
_16="N";
}else{
_16="S";
}
}else{
if(_15=="LON"){
if(_14>=0){
_16="E";
}else{
_16="W";
}
}
}
return _16;
};
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function MapScaleText(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.submitForm=function(){
var _3=document.getElementById(this.formName);
var _4=_3.mapScale.value;
this.model.map.zoomToScale(_4.split(",").join(""));
return false;
};
this.handleKeyPress=function(_5){
var _6;
var _7;
if(_5){
_6=_5.which;
_7=_5.currentTarget;
}else{
_6=window.event.keyCode;
_7=window.event.srcElement.form;
}
if(_6==13){
_7.parentWidget.submitForm();
return false;
}
};
this.showScale=function(_8){
var _9=document.getElementById(_8.formName);
if(_9){
var _a=Math.round(_8.model.map.getScale());
var _b=new Array();
while(_a>=1000){
var _c=_a/1000;
_a=Math.floor(_c);
var _d=leadingZeros(Math.round((_c-_a)*1000).toString(),3);
_b.unshift(_d);
}
_b.unshift(_a);
_9.mapScale.value=_b.join(",");
}
};
this.model.addListener("bbox",this.showScale,this);
this.model.addListener("refresh",this.showScale,this);
this.prePaint=function(_e){
var _f=_e.model.extent.getScale();
this.stylesheet.setParameter("mapScale",_f);
};
this.postPaint=function(_10){
var _11=document.getElementById(_10.formName);
_11.parentWidget=_10;
_11.onkeypress=_10.handleKeyPress;
_10.showScale(_10);
};
this.formName="MapScaleText_"+mbIds.getId();
this.stylesheet.setParameter("formName",this.formName);
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBase.js");
function Loading2(_1,_2){
WidgetBase.apply(this,new Array(_1,_2));
this.imageSrc=config.skinDir+this.getProperty("mb:imageSrc","/images/Loading.gif");
this.textMessage=this.getProperty("mb:textMessage",mbGetMessage("docLoading"));
this.updateMessage=this.textMessage;
this.mapContainerNode=_1.selectSingleNode("mb:mapContainerId");
if(!this.mapContainerNode){
this.mapContainerNode=_1.selectSingleNode("mb:targetModel");
}
if(this.mapContainerNode){
this.containerNodeId=getNodeValue(this.mapContainerNode);
this.htmlTagId=this.containerNodeId;
}
this.model.addListener("newModel",this.paint,this);
this.model.addListener("loadModel",this.clear,this);
this.model.addListener("refresh",this.paint,this);
this.model.addListener("modelStatus",this.update,this);
}
Loading2.prototype.paint=function(_3){
var _4=_3.getNode();
if(_4){
if(_3.model.template){
return;
}
if(!_3.model.url&&!_3.mapContainerNode){
return;
}
var _5=document.getElementById(_3.outputNodeId+"_loading");
if(!_5){
_5=document.createElement("div");
_5.setAttribute("id",_3.outputNodeId+"_loading");
_4.appendChild(_5);
}
_5.className="loadingIndicator";
_5.style.zIndex=10001;
if(_3.mapContainerNode){
_5.style.position="absolute";
_5.style.left="0px";
_5.style.top="0px";
}
if(_3.imageSrc){
var _6=document.getElementById(_3.outputNodeId+"_imageNode");
if(!_6){
_6=document.createElement("img");
_6.setAttribute("id",_3.outputNodeId+"_imageNode");
_5.appendChild(_6);
_6.style.zIndex=10001;
}
_6.src=_3.imageSrc;
}
if(_3.updateMessage){
var _7=document.getElementById(_3.outputNodeId+"_messageNode");
if(!_7){
_7=document.createElement("p");
_7.setAttribute("id",_3.outputNodeId+"_messageNode");
_5.appendChild(_7);
}
_7.innerHTML=_3.updateMessage;
}
}
};
Loading2.prototype.clear=function(_8,_9){
var _a=document.getElementById(_8.outputNodeId+"_loading");
var _b=_8.getNode();
if(_b&&_a){
_b.removeChild(_a);
}
};
Loading2.prototype.update=function(_c,_d){
_c.updateMessage=_d||null;
if(_d){
_c.paint(_c);
}else{
_c.clear(_c);
}
};

mapbuilder.loadScript(baseDir+"/widget/WidgetBase.js");
function PanZoomBar(_1,_2){
WidgetBase.apply(this,new Array(_1,_2));
this.cursor="crosshair";
this.init=function(_3){
_3.model.map.addControl(new OpenLayers.Control.PanZoomBar());
_3.model.map.addControl(new OpenLayers.Control.Navigation());
_3.model.map.addControl(new OpenLayers.Control.KeyboardDefaults());
};
this.model.addListener("refresh",this.init,this);
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function EditButtonBase(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.cursor="crosshair";
this.trm=this.getProperty("mb:transactionResponseModel");
this.defaultModelUrl=this.getProperty("mb:defaultModelUrl");
this.featureXpath=this.getProperty("mb:featureXpath");
this.appendOnEdit=Mapbuilder.parseBoolean(this.getProperty("mb:appendOnEdit",true));
this.createControl=function(_3){
return OpenLayers.Control.DrawFeature;
};
this.doSelect=function(_4,_5){
if(_4.control.active){
if(_4.trm&&!_4.transactionResponseModel){
_4.transactionResponseModel=window.config.objects[_4.trm];
}
if(!_5&&_4.transactionResponseModel){
_4.transactionResponseModel.setModel(_4.transactionResponseModel,null);
}
config.loadModel(_4.targetModel.id,_4.defaultModelUrl);
}
};
this.newSession=function(_6){
_6.modified=false;
};
this.handleFeatureInsert=function(_7){
var _8=_7.layer.mbButton;
_8.feature=OpenLayers.Util.extend({},_7);
_8.geometry=OpenLayers.Util.extend({},_7.geometry);
var _9=_8.targetModel.doc.selectSingleNode("/*/*").cloneNode(true);
if(_8.modified&&_8.appendOnEdit){
_8.targetModel.doc.selectSingleNode("/*").appendChild(_9);
}
_8.setFeature(_8);
_8.modified=true;
_7.mbSelectStyle=null;
_7.destroy();
};
this.setEditingLayer=function(_a){
if(!_a.targetContext.featureLayers[_a.id]){
_a.featureLayer=new OpenLayers.Layer.Vector(_a.id);
_a.featureLayer.calculateInRange=function(){
return true;
};
_a.featureLayer.mbButton=_a;
_a.targetContext.featureLayers[_a.id]=_a.featureLayer;
_a.featureLayer.onFeatureInsert=_a.handleFeatureInsert;
}
};
this.setFeature=function(_b){
if(!_b.enabled){
return;
}
var _c=new OpenLayers.Format.GML().write([_b.feature]);
var _d=Sarissa.getDomDocument();
_d=(new DOMParser()).parseFromString(_c,"text/xml");
if(_b.targetModel.namespace){
Sarissa.setXpathNamespaces(_d,_b.targetModel.namespace);
}
var _e=_b.featureXpath.lastIndexOf("/")+1;
var _f=_b.featureXpath.substring(_e);
var _10=_d.selectSingleNode("//"+_f);
if(!_10){
_10=_d.selectSingleNode("//gml:featureMember/*/*");
}
var _11=_b.targetModel.doc.selectSingleNode(_b.featureXpath);
if(_11.childNodes.length>0){
_11.removeChild(_11.firstChild);
}
try{
Sarissa.copyChildNodes(_10,_11);
}
catch(e){
alert(mbGetMessage("invalidFeatureXpathEditLine",_b.featureXpath));
}
_b.targetModel.setParam("refresh");
};
this.initButton=function(_12){
if(!_12.targetContext.featureLayers){
_12.targetContext.featureLayers=new Array();
}
_12.targetContext.addFirstListener("refresh",_12.setEditingLayer,_12);
_12.targetModel.addListener("loadModel",_12.newSession,_12);
};
this.model.addListener("init",this.initButton,this);
}

mapbuilder.loadScript(baseDir+"/widget/EditButtonBase.js");
function EditLine(_1,_2){
EditButtonBase.apply(this,new Array(_1,_2));
this.instantiateControl=function(_3,_4){
return new _4(_3.featureLayer,OpenLayers.Handler.Path);
};
}

mapbuilder.loadScript(baseDir+"/widget/EditButtonBase.js");
function EditPoint(_1,_2){
EditButtonBase.apply(this,new Array(_1,_2));
this.instantiateControl=function(_3,_4){
return new _4(_3.featureLayer,OpenLayers.Handler.Point);
};
}

mapbuilder.loadScript(baseDir+"/widget/EditButtonBase.js");
function EditPolygon(_1,_2){
EditButtonBase.apply(this,new Array(_1,_2));
this.instantiateControl=function(_3,_4){
return new _4(_3.featureLayer,OpenLayers.Handler.Polygon);
};
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function InsertFeature(_1,_2){
this.cursor="default";
ButtonBase.apply(this,new Array(_1,_2));
this.trm=this.getProperty("mb:transactionResponseModel");
this.tm=this.getProperty("mb:targetModel");
this.tc=this.getProperty("mb:targetContext");
this.httpPayload=new Object();
this.httpPayload.url=this.getProperty("mb:webServiceUrl");
this.httpPayload.method="post";
this.insertXsl=new XslProcessor(baseDir+"/tool/xsl/wfs_Insert.xsl");
this.updateXsl=new XslProcessor(baseDir+"/tool/xsl/wfs_Update.xsl");
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{CLASS_NAME:"mbInsertFeature",type:OpenLayers.Control.TYPE_BUTTON});
return _4;
};
this.doSelect=function(_5,_6){
if(_6){
if(!_5.transactionResponseModel){
_5.transactionResponseModel=window.config.objects[_5.trm];
_5.transactionResponseModel.addListener("loadModel",_5.handleResponse,_5);
}
if(!_5.targetModel){
_5.targetModel=window.config.objects[_5.tm];
}
if(!_5.targetContext){
_5.targetContext=window.config.objects[_5.tc];
}
fid=_5.targetModel.getXpathValue(_5.targetModel,"//@fid");
if(_5.targetModel.doc){
if(fid){
s=_5.updateXsl.transformNodeToObject(_5.targetModel.doc);
}else{
s=_5.insertXsl.transformNodeToObject(_5.targetModel.doc);
}
_5.httpPayload.postData=s;
_5.transactionResponseModel.transactionType="insert";
_5.transactionResponseModel.newRequest(_5.transactionResponseModel,_5.httpPayload);
}else{
alert(mbGetMessage("noFeatureToInsert"));
}
}
};
this.handleResponse=function(_7){
if(_7.transactionResponseModel.transactionType=="insert"){
success=_7.transactionResponseModel.doc.selectSingleNode("//wfs:TransactionResult/wfs:Status/wfs:SUCCESS");
if(success){
config.loadModel(_7.targetModel.id,_7.targetModel.url);
_7.targetContext.callListeners("refreshWmsLayers");
}
}
};
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function DeleteFeature(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.cursor="default";
this.trm=this.getProperty("mb:transactionResponseModel");
this.tm=this.getProperty("mb:targetModel");
this.tc=this.getProperty("mb:targetContext");
this.httpPayload=new Object();
this.httpPayload.url=this.getProperty("mb:webServiceUrl");
this.httpPayload.method="post";
this.deleteXsl=new XslProcessor(baseDir+"/tool/xsl/wfs_Delete.xsl");
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{CLASS_NAME:"mbDeleteFeature",type:OpenLayers.Control.TYPE_BUTTON});
return _4;
};
this.doSelect=function(_5,_6){
if(_6){
if(!_5.transactionResponseModel){
_5.transactionResponseModel=window.config.objects[_5.trm];
_5.transactionResponseModel.addListener("loadModel",_5.handleResponse,_5);
}
if(!_5.targetModel){
_5.targetModel=window.config.objects[_5.tm];
}
if(!_5.targetContext){
_5.targetContext=window.config.objects[_5.tc];
}
fid=_5.targetModel.getXpathValue(_5.targetModel,"//@fid");
if(_5.targetModel.doc&&fid){
s=_5.deleteXsl.transformNodeToObject(_5.targetModel.doc);
_5.httpPayload.postData=s;
_5.transactionResponseModel.transactionType="delete";
_5.transactionResponseModel.newRequest(_5.transactionResponseModel,_5.httpPayload);
}else{
alert(mbGetMessage("noFeatureToDelete"));
}
}
};
this.handleResponse=function(_7){
if(_7.transactionResponseModel.transactionType=="delete"){
success=_7.transactionResponseModel.doc.selectSingleNode("//wfs:TransactionResult/wfs:Status/wfs:SUCCESS");
if(success){
_7.targetModel.setModel(_7.targetModel,null);
_7.targetModel.callListeners("refreshGmlRenderers");
_7.targetContext.callListeners("refreshWmsLayers");
}
}
};
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
function WfsGetFeature(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.widgetNode=_1;
this.trm=this.getProperty("mb:transactionResponseModel");
this.httpPayload=new Object({method:"get",postData:null});
this.typeName=this.getProperty("mb:typeName");
this.maxFeatures=this.getProperty("mb:maxFeatures",1);
this.webServiceUrl=this.getProperty("mb:webServiceUrl");
this.webServiceUrl+=this.webServiceUrl.indexOf("?")>-1?"&":"?";
this.webServiceSrs=new OpenLayers.Projection(this.getProperty("mb:webServiceSrs","EPSG:4326"));
this.cursor="pointer";
this.createControl=function(_3){
var _4=config.objects[_3.trm];
var _5=OpenLayers.Class(OpenLayers.Control,{CLASS_NAME:"mbControl.WfsGetFeature",type:OpenLayers.Control.TYPE_TOOL,tolerance:new Number(_3.getProperty("mb:tolerance")),httpPayload:_3.httpPayload,maxFeatures:_3.maxFeatures,webServiceUrl:_3.webServiceUrl,transactionResponseModel:_4,draw:function(){
this.handler=new OpenLayers.Handler.Box(this,{done:this.selectBox},{keyMask:this.keyMask});
},selectBox:function(_6){
var _7,minXY,maxXY;
if(_6 instanceof OpenLayers.Bounds){
minXY=this.map.getLonLatFromPixel(new OpenLayers.Pixel(_6.left,_6.bottom));
maxXY=this.map.getLonLatFromPixel(new OpenLayers.Pixel(_6.right,_6.top));
}else{
minXY=this.map.getLonLatFromPixel(new OpenLayers.Pixel(_6.x-this.tolerance,_6.y+this.tolerance));
maxXY=this.map.getLonLatFromPixel(new OpenLayers.Pixel(_6.x+this.tolerance,_6.y-this.tolerance));
}
_7=new OpenLayers.Bounds(minXY.lon,minXY.lat,maxXY.lon,maxXY.lat);
if(this.map.projection.projCode!=this.objRef.webServiceSrs.projCode){
_7.transform(this.map.projection,this.objRef.webServiceSrs);
}
var _8=_3.typeName;
if(!_8){
var _9=_3.targetModel.getQueryableLayers();
if(_9.length==0){
alert(mbGetMessage("noQueryableLayers"));
return;
}else{
_8="";
for(var i=0;i<_9.length;++i){
var _b=_9[i];
var _c=_b.selectSingleNode("wmc:Name");
_c=(_c)?getNodeValue(_c):"";
var _d=_b.getAttribute("id")||_c;
var _e=_3.targetModel.getHidden(_d);
if(_e==0){
if(_8!=""){
_8+=",";
}
_8+=_c;
}
}
}
}
if(_8==""){
alert(mbGetMessage("noQueryableLayersVisible"));
return;
}
this.httpPayload.url=this.webServiceUrl+OpenLayers.Util.getParameterString({SERVICE:"WFS",VERSION:"1.0.0",REQUEST:"GetFeature",TYPENAME:_8,MAXFEATURES:this.maxFeatures,BBOX:_7.toBBOX()});
this.transactionResponseModel.newRequest(this.transactionResponseModel,this.httpPayload);
}});
return _5;
};
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function Legend(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.model.addListener("deleteLayer",this.refresh,this);
this.model.addListener("moveLayerUp",this.refresh,this);
this.model.addListener("moveLayerDown",this.refresh,this);
if(this.autoRefresh){
this.model.addListener("addLayer",this.refresh,this);
}
this.prePaint=function(_3){
if(_3.model.featureName){
_3.stylesheet.setParameter("featureName",_3.model.featureName);
_3.stylesheet.setParameter("hidden",_3.model.getHidden(_3.model.featureName).toString());
}
_3.visibleLayer=Mapbuilder.getProperty(_3.model.doc,_3.model.nodeSelectXpath+"[@hidden='0' and @opaque='1']/wmc:Name");
};
}
Legend.prototype.refresh=function(_4,_5){
_4.paint(_4,_4.id);
};
Legend.prototype.selectLayer=function(_6,_7){
_6.model.setParam("selectedLayer",_7);
};
Legend.prototype.swapOpaqueLayer=function(_8){
this.model.setHidden(this.visibleLayer,true);
this.model.setHidden(_8,false);
this.visibleLayer=_8;
};

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function Version(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
}

mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
mapbuilder.loadScript(baseDir+"/widget/WidgetBase.js");
function OverviewMap(_1,_2){
WidgetBase.apply(this,new Array(_1,_2));
var _3=this.getProperty("mb:width");
if(_3){
this.width=new Number(_3);
}
var _4=this.getProperty("mb:height");
if(_4){
this.height=new Number(_4);
}
var _5=this.getProperty("mb:minRatio");
if(_5){
this.minRatio=new Number(_5);
}
var _6=this.getProperty("mb:maxRatio");
if(_6){
this.maxRatio=new Number(_6);
}
var _7=_1.selectSingleNode("mb:layers");
if(_7){
this.layerNames=new Array();
var _8=_7.childNodes;
for(var i=0;i<_8.length;i++){
if(_8[i].firstChild){
this.layerNames.push(getNodeValue(_8[i]));
}
}
}
this.model.addListener("refresh",this.addOverviewMap,this);
}
OverviewMap.prototype.addOverviewMap=function(_a){
if(_a.model&&_a.model.map){
var _b=_a.model.map;
this.control=null;
var _c={div:_a.getNode(),objRef:this,destroy:function(){
OpenLayers.Control.OverviewMap.prototype.destroy.apply(this,arguments);
this.div=null;
_a.control=null;
_a=null;
},layers:new Array()};
if(_a.minRatio){
_c.minRatio=_a.minRatio;
}
if(_a.maxRatio){
_c.maxRatio=_a.maxRatio;
}
if(!_a.layerNames){
for(var i in _b.mbMapPane.oLlayers){
var _e=_b.mbMapPane.oLlayers[i];
if(_e){
var _f=_a.getClonedLayer(_e,true);
_c.layers.push(_f);
break;
}
}
}
var _10=true;
if(_a.layerNames){
for(var i=0;i<_a.layerNames.length;i++){
var _e=_b.mbMapPane.getLayer(_b.mbMapPane,_a.layerNames[i]);
if(_e){
_c.layers.push(_a.getClonedLayer(_e,_10));
_10=false;
}
}
}
var _11=_b.getExtent();
if(_a.width&&_a.height){
_c.size=new OpenLayers.Size(_a.width,_a.height);
}else{
if(_a.width){
_c.size=new OpenLayers.Size(_a.width,_a.width*_11.getHeight()/_11.getWidth());
}else{
if(_a.height){
_c.size=new OpenLayers.Size(_a.height*_11.getWidth()/_11.getHeight(),_a.height);
}
}
}
if(!_a.control){
_a.control=new OpenLayers.Control.OverviewMap(_c);
_a.control.mapOptions={theme:null};
_b.addControl(_a.control);
}
for(var i=0;i<_c.layers.length;i++){
_c.layers[i].setVisibility(true);
}
}
};
OverviewMap.prototype.getClonedLayer=function(_12,_13){
if(_12==null){
return null;
}
_13=_13?true:false;
if(_12 instanceof OpenLayers.Layer.WMS){
var _14={units:_12.units,projection:_12.projection,maxExtent:_12.maxExtent,maxResolution:"auto",ratio:1,singleTile:true,isBaseLayer:_13};
return new OpenLayers.Layer.WMS(_12.name,_12.url,{layers:_12.params.LAYERS,format:_12.params.FORMAT,transparent:_12.params.TRANSPARENT,sld:_12.params.SLD,sld_body:_12.params.SLD_BODY,styles:_12.params.STYLES},_14);
}else{
var _15=_12.clone();
_15.setVisibility(true);
return _15;
}
};

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function TransactionResponse(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
}

mapbuilder.loadScript(baseDir+"/model/ModelBase.js");
function Model(_1,_2){
ModelBase.apply(this,new Array(_1,_2));
}

mapbuilder.loadScript(baseDir+"/model/ModelBase.js");
function Context(_1,_2){
ModelBase.apply(this,new Array(_1,_2));
this.namespace="xmlns:mb='http://mapbuilder.sourceforge.net/mapbuilder' xmlns:wmc='http://www.opengis.net/context' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'";
this.setHidden=function(_3,_4){
var _5="0";
if(_4){
_5="1";
}
var _6=this.getLayer(_3);
if(_6){
_6.setAttribute("hidden",_5);
}
this.callListeners("hidden",_3);
};
this.getHidden=function(_7){
var _8=1;
var _9=this.getLayer(_7);
if(_9){
_8=_9.getAttribute("hidden");
}
return _8;
};
this.getBoundingBox=function(){
var _a=new Array();
var _b=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:BoundingBox");
_a[0]=parseFloat(_b.getAttribute("minx"));
_a[1]=parseFloat(_b.getAttribute("miny"));
_a[2]=parseFloat(_b.getAttribute("maxx"));
_a[3]=parseFloat(_b.getAttribute("maxy"));
return _a;
};
this.setBoundingBox=function(_c){
var _d=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:BoundingBox");
_d.setAttribute("minx",_c[0]);
_d.setAttribute("miny",_c[1]);
_d.setAttribute("maxx",_c[2]);
_d.setAttribute("maxy",_c[3]);
this.callListeners("bbox",_c);
};
this.initBbox=function(_e){
if(window.cgiArgs["bbox"]){
var _f=window.cgiArgs["bbox"].split(",");
_e.setBoundingBox(_f);
}
};
this.addListener("loadModel",this.initBbox,this);
this.initAoi=function(_10){
if(window.cgiArgs["aoi"]){
var aoi=window.cgiArgs["aoi"].split(",");
_10.setParam("aoi",new Array(new Array(aoi[0],aoi[3]),new Array(aoi[2],aoi[1])));
}
};
this.addListener("loadModel",this.initAoi,this);
this.setSRS=function(srs){
var _13=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:BoundingBox");
_13.setAttribute("SRS",srs);
this.callListeners("srs");
};
this.getSRS=function(){
var _14=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:BoundingBox");
srs=_14.getAttribute("SRS");
srs=srs?srs:"EPSG:4326";
return srs;
};
this.initProj=function(_15){
_15.proj=new OpenLayers.Projection(_15.getSRS());
};
this.addFirstListener("loadModel",this.initProj,this);
this.getWindowWidth=function(){
var win=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:Window");
return win.getAttribute("width");
};
this.setWindowWidth=function(_17){
var win=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:Window");
win.setAttribute("width",_17);
this.callListeners("resize");
};
this.getWindowHeight=function(){
var win=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:Window");
return win.getAttribute("height");
};
this.setWindowHeight=function(_1a){
var win=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:Window");
win.setAttribute("height",_1a);
this.callListeners("resize");
};
this.getWindowSize=function(){
var win=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:Window");
return new Array(win.getAttribute("width"),win.getAttribute("height"));
};
this.setWindowSize=function(_1d){
var _1e=_1d[0];
var _1f=_1d[1];
var win=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:Window");
win.setAttribute("width",_1e);
win.setAttribute("height",_1f);
this.callListeners("resize");
};
this.getFeatureNode=function(_21){
return this.doc.selectSingleNode(this.nodeSelectXpath+"[wmc:Name='"+_21+"']");
};
this.getServerUrl=function(_22,_23,_24){
return _24.selectSingleNode("wmc:Server/wmc:OnlineResource").getAttribute("xlink:href");
};
this.getVersion=function(_25){
return _25.selectSingleNode("wmc:Server").getAttribute("version");
};
this.getMethod=function(_26){
return _26.selectSingleNode("wmc:Server/wmc:OnlineResource").getAttribute("wmc:method");
};
this.getQueryableLayers=function(){
var _27=this.doc.selectNodes("/wmc:ViewContext/wmc:LayerList/wmc:Layer[attribute::queryable='1']");
return _27;
};
this.getAllLayers=function(){
var _28=this.doc.selectNodes("/wmc:ViewContext/wmc:LayerList/wmc:Layer");
return _28;
};
this.getLayer=function(_29){
var _2a=this.doc.selectSingleNode("/wmc:ViewContext/wmc:LayerList/wmc:Layer[@id='"+_29+"']");
if(_2a==null){
_2a=this.doc.selectSingleNode("/wmc:ViewContext/wmc:LayerList/wmc:Layer[wmc:Name='"+_29+"']");
}
return _2a;
};
this.getLayerIdByName=function(_2b){
var _2c=this.getLayer(_2b);
var id;
if(_2c){
id=_2c.getAttribute("id");
}
return id||false;
};
this.addLayer=function(_2e,_2f){
var _30=_2e.doc.selectSingleNode("/wmc:ViewContext/wmc:LayerList");
_30.appendChild(_2f);
if(!_2f.getAttribute("id")){
var _31=Math.round(10000*Math.random());
id=Mapbuilder.getProperty(_2f,"wmc:Name")+"_"+_31;
_2f.setAttribute("id",id);
}
_2e.modified=true;
};
this.addFirstListener("addLayer",this.addLayer,this);
this.addSLD=function(_32,_33){
var _34=Mapbuilder.getProperty(_33,"//Name");
var _35=_32.doc.selectSingleNode("//wmc:Layer[wmc:Name='"+_34+"']");
_35.appendChild(_33.cloneNode(true));
_32.modified=true;
var _36=[];
_36["sld_body"]=(new XMLSerializer()).serializeToString(_32.doc.selectSingleNode("//wmc:Layer[wmc:Name='"+_34+"']/wmc:StyleList/wmc:Style/wmc:SLD/wmc:StyledLayerDescriptor"));
_32.map.mbMapPane.refreshLayer(_32.map.mbMapPane,_34,_36);
};
this.addFirstListener("addSLD",this.addSLD,this);
this.deleteLayer=function(_37,_38){
var _39=_37.getLayer(_38);
if(!_39){
alert(mbGetMessage("nodeNotFound",_38));
return;
}
_39.parentNode.removeChild(_39);
_37.modified=true;
};
this.addFirstListener("deleteLayer",this.deleteLayer,this);
this.moveLayerUp=function(_3a,_3b){
var _3c=_3a.getLayer(_3b);
var _3d=_3c.selectSingleNode("following-sibling::*");
if(!_3d){
alert(mbGetMessage("cantMoveUp",_3b));
return;
}
_3c.parentNode.insertBefore(_3d,_3c);
_3a.modified=true;
};
this.addFirstListener("moveLayerUp",this.moveLayerUp,this);
this.moveLayerDown=function(_3e,_3f){
var _40=_3e.getLayer(_3f);
var _41=_40.selectNodes("preceding-sibling::*");
var _42=_41[_41.length-1];
if(!_42){
alert(mbGetMessage("cantMoveDown",_3f));
return;
}
_40.parentNode.insertBefore(_40,_42);
_3e.modified=true;
};
this.addFirstListener("moveLayerDown",this.moveLayerDown,this);
this.setExtension=function(_43){
var _44=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:Extension");
if(!_44){
var _45=this.doc.selectSingleNode("/wmc:ViewContext/wmc:General");
var _46=createElementWithNS(this.doc,"Extension","http://www.opengis.net/context");
_44=_45.appendChild(_46);
}
return _44.appendChild(_43);
};
this.getExtension=function(){
return this.doc.selectSingleNode("/wmc:ViewContext/wmc:General/wmc:Extension");
};
this.initTimeExtent=function(_47){
var _48=_47.doc.selectNodes("//wmc:Dimension[@name='time']");
for(var i=0;i<_48.length;++i){
var _4a=_48[i];
_47.timestampList=createElementWithNS(_47.doc,"TimestampList",mbNsUrl);
var _4b;
var _4c=_4a.parentNode.parentNode;
if(_4c.selectSingleNode("@id")){
_4b=Mapbuilder.getProperty(_4c,"@id");
}else{
_4b=Mapbuilder.getProperty(_4c,"wmc:Name");
}
_47.timestampList.setAttribute("layerId",_4b);
var _4d=getNodeValue(_4a).split(",");
for(var j=0;j<_4d.length;++j){
var _4f=_4d[j].split("/");
if(_4f.length==3){
var _50=setISODate(_4f[0]);
var _51=setISODate(_4f[1]);
var _52=_4f[2];
var _53=_52.match(/^P((\d*)Y)?((\d*)M)?((\d*)D)?T?((\d*)H)?((\d*)M)?((.*)S)?/);
for(var i=1;i<_53.length;++i){
if(!_53[i]){
_53[i]=0;
}
}
do{
var _54=createElementWithNS(_47.doc,"Timestamp",mbNsUrl);
_54.appendChild(_47.doc.createTextNode(getISODate(_50)));
_47.timestampList.appendChild(_54);
_50.setFullYear(_50.getFullYear()+parseInt(_53[2],10));
_50.setMonth(_50.getMonth()+parseInt(_53[4],10));
_50.setDate(_50.getDate()+parseInt(_53[6],10));
_50.setHours(_50.getHours()+parseInt(_53[8],10));
_50.setMinutes(_50.getMinutes()+parseInt(_53[10],10));
_50.setSeconds(_50.getSeconds()+parseFloat(_53[12]));
}while(_50.getTime()<=_51.getTime());
}else{
var _54=createElementWithNS(_47.doc,"Timestamp",mbNsUrl);
_54.appendChild(_47.doc.createTextNode(_4d[j]));
_47.timestampList.appendChild(_54);
}
}
_47.setExtension(_47.timestampList);
}
};
this.addFirstListener("loadModel",this.initTimeExtent,this);
this.clearTimeExtent=function(_55){
var _56=_55.timestampList;
if(_56){
_56.parentNode.removeChild(_56);
}
};
this.addListener("newModel",this.clearTimeExtent,this);
this.getCurrentTimestamp=function(_57){
var _58=this.getParam("timestamp");
return getNodeValue(this.timestampList.childNodes[_58]);
};
this.setOpacity=function(_59,_5a){
var _5b=this.getLayer(_59);
if(_5b){
_5b.setAttribute("opacity",_5a);
}
this.callListeners("opacity",_59);
};
this.getOpacity=function(_5c){
var _5d=1;
var _5e=this.getLayer(_5c);
if(_5e){
_5d=_5e.getAttribute("opacity");
}
return _5d;
};
}

mapbuilder.loadScript(baseDir+"/model/ModelBase.js");
function OwsContext(_1,_2){
ModelBase.apply(this,new Array(_1,_2));
this.namespace=this.namespace?this.namespace.replace(/\"/g,"'")+" ":"";
this.namespace=this.namespace+"xmlns:wmc='http://www.opengis.net/context' xmlns:ows='http://www.opengis.net/ows' xmlns:ogc='http://www.opengis.net/ogc' xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:gml='http://www.opengis.net/gml' xmlns:wfs='http://www.opengis.net/wfs' xmlns:sld='http://www.opengis.net/sld'";
this.setHidden=function(_3,_4){
var _5="0";
if(_4){
_5="1";
}
var _6=this.getLayer(_3);
if(_6){
_6.setAttribute("hidden",_5);
}
this.callListeners("hidden",_3);
};
this.getHidden=function(_7){
var _8=1;
var _9=this.getFeatureNode(_7);
if(_9){
_8=_9.getAttribute("hidden");
}
return _8;
};
this.getBoundingBox=function(){
var _a=new Array();
var _b=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/ows:BoundingBox/ows:LowerCorner");
var _c=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/ows:BoundingBox/ows:UpperCorner");
var _d=new String(getNodeValue(_b)+" "+getNodeValue(_c)).split(" ");
for(i=0;i<_d.length;++i){
_a[i]=parseFloat(_d[i]);
}
return _a;
};
this.setBoundingBox=function(_e){
var _f=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/ows:BoundingBox/ows:LowerCorner");
_f.firstChild.nodeValue=_e[0]+" "+_e[1];
var _10=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/ows:BoundingBox/ows:UpperCorner");
_10.firstChild.nodeValue=_e[2]+" "+_e[3];
this.callListeners("bbox",_e);
};
this.initBbox=function(_11){
if(window.cgiArgs["bbox"]){
var _12=window.cgiArgs["bbox"].split(",");
_11.setBoundingBox(_12);
}
};
this.addListener("loadModel",this.initBbox,this);
this.initAoi=function(_13){
if(window.cgiArgs["aoi"]){
var aoi=window.cgiArgs["aoi"].split(",");
_13.setParam("aoi",new Array(new Array(aoi[0],aoi[3]),new Array(aoi[2],aoi[1])));
}
};
this.addListener("loadModel",this.initAoi,this);
this.setSRS=function(srs){
var _16=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/ows:BoundingBox");
_16.setAttribute("crs",srs);
this.callListeners("srs");
};
this.getSRS=function(){
var _17=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/ows:BoundingBox");
srs=_17.getAttribute("crs");
srs=srs?srs:"EPSG:4326";
return srs;
};
this.initProj=function(_18){
_18.proj=new OpenLayers.Projection(_18.getSRS());
};
this.addFirstListener("loadModel",this.initProj,this);
this.getWindowWidth=function(){
var win=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/wmc:Window");
return win.getAttribute("width");
};
this.setWindowWidth=function(_1a){
var win=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/wmc:Window");
win.setAttribute("width",_1a);
this.callListeners("resize");
};
this.getWindowHeight=function(){
var win=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/wmc:Window");
return win.getAttribute("height");
};
this.setWindowHeight=function(_1d){
var win=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/wmc:Window");
win.setAttribute("height",_1d);
this.callListeners("resize");
};
this.getWindowSize=function(){
var win=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/wmc:Window");
return new Array(win.getAttribute("width"),win.getAttribute("height"));
};
this.setWindowSize=function(_20){
var win=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/wmc:Window");
var _22=_20[0];
var _23=_20[1];
win.setAttribute("width",_22);
win.setAttribute("height",_23);
this.callListeners("resize");
};
this.getFeatureNode=function(_24){
if(this.doc){
var _25=this.doc.selectSingleNode("//wmc:ResourceList/*[@id='"+_24+"']");
if(_25==null){
_25=this.doc.selectSingleNode("//wmc:ResourceList/*[wmc:Name='"+_24+"']");
}
if(_25==null){
alert(mbGetMessage("featureNotFoundOwsContext"));
}
return _25;
}
};
this.getServerUrl=function(_26,_27,_28){
var _29=_26.split(":");
if(_29.length>0){
_29=_29[0].toUpperCase();
}
var url=_28.selectSingleNode("wmc:Server[@service='OGC:"+_29+"']/wmc:OnlineResource").getAttribute("xlink:href");
if(!url){
url=_28.selectSingleNode("wmc:Server/wmc:OnlineResource").getAttribute("xlink:href");
}
return url;
};
this.getVersion=function(_2b){
return _2b.selectSingleNode("wmc:Server").getAttribute("version");
};
this.getMethod=function(_2c){
return _2c.selectSingleNode("wmc:Server/wmc:OnlineResource").getAttribute("wmc:method");
};
this.getBaseLayerService=function(){
x=this.doc.selectSingleNode("/wmc:OWSContext/wmc:ResourceList/wmc:Layer[last()]/wmc:Server");
s=x.getAttribute("service");
return s;
};
this.loadFeatures=function(_2d){
var _2e=_2d.nodeSelectXpath+"/wmc:FeatureType[wmc:Server/@service='OGC:WFS']/wmc:Name";
var _2f=_2d.doc.selectNodes(_2e);
for(var i=0;i<_2f.length;i++){
var _31=getNodeValue(_2f[i]);
_2d.setParam("wfs_GetFeature",_31);
}
};
this.addListener("loadModel",this.loadFeatures,this);
this.setRequestParameters=function(_32,_33){
var _34=this.getFeatureNode(_32);
if(_34.selectSingleNode("ogc:Filter")){
_33.setParameter("filter",escape((new XMLSerializer()).serializeToString(_34.selectSingleNode("ogc:Filter"))));
}
};
this.getQueryableLayers=function(){
var _35=this.doc.selectNodes("/wmc:OWSContext/wmc:ResourceList/wmc:Layer[@queryable='1']|/wmc:OWSContext/wmc:ResourceList/wmc:FeatureType[@queryable='1']");
if(_35==null){
_35=this.doc.selectNodes("/wmc:OWSContext/wmc:ResourceList/wmc:Layer|/wmc:OWSContext/wmc:ResourceList/wmc:Layer");
}
return _35;
};
this.getAllLayers=function(){
var _36=this.doc.selectNodes("//wmc:Layer|//wmc:FeatureType");
return _36;
};
this.getLayer=function(_37){
var _38=this.doc.selectSingleNode("/wmc:OWSContext/wmc:ResourceList/wmc:FeatureType[@id='"+_37+"']");
if(_38==null){
_38=this.doc.selectSingleNode("/wmc:OWSContext/wmc:ResourceList/wmc:Layer[@id='"+_37+"']");
}
if(_38==null){
_38=this.doc.selectSingleNode("/wmc:OWSContext/wmc:ResourceList/wmc:Layer[wmc:Name='"+_37+"']");
}
if(_38==null){
_38=this.doc.selectSingleNode("/wmc:OWSContext/wmc:ResourceList/wmc:FeatureType[wmc:Name='"+_37+"']");
}
if(_38==null){
_38=this.doc.selectSingleNode("/wmc:OWSContext/wmc:ResourceList/wmc:RssLayer[@id='"+_37+"']");
}
return _38;
};
this.getLayerIdByName=function(_39){
var _3a=this.getLayer(_39);
var id;
if(_3a){
id=_3a.getAttribute("id");
}
return id||false;
};
this.addLayer=function(_3c,_3d){
if(_3c.doc!=null){
var _3e=_3c.doc.selectSingleNode("/wmc:OWSContext/wmc:ResourceList");
var _3f=Math.round(10000*Math.random());
id=Mapbuilder.getProperty(_3d,"wmc:Name")+"_"+_3f;
_3d.setAttribute("id",id);
var id=_3d.getAttribute("id");
var str="/wmc:OWSContext/wmc:ResourceList/"+_3d.nodeName+"[@id='"+id+"']";
var _42=_3c.doc.selectSingleNode(str);
if(_42!=null){
_3e.removeChild(_42);
}
_3e.appendChild(_3d);
_3c.modified=true;
if(this.debug){
mbDebugMessage("Adding layer:"+(new XMLSerializer()).serializeToString(_3d));
}
}else{
alert(mbGetMessage("nullOwsContext"));
}
};
this.addFirstListener("addLayer",this.addLayer,this);
this.getBaseLayer=function(){
var _43=this.doc.selectSingleNode("/wmc:OWSContext/wmc:ResourceList/ows:BaseLayer");
return _43;
};
this.addSLD=function(_44,_45){
var _46=Mapbuilder.getProperty(_45,"//Name");
var _47=_44.doc.selectSingleNode("//wmc:Layer[wmc:Name='"+_46+"']");
_47.appendChild(_45.cloneNode(true));
_44.modified=true;
var _48=[];
_48["sld_body"]=(new XMLSerializer()).serializeToString(_44.doc.selectSingleNode("//wmc:Layer[wmc:Name='"+_46+"']/wmc:StyleList/wmc:Style/wmc:SLD/wmc:StyledLayerDescriptor"));
_44.map.mbMapPane.refreshLayer(_44.map.mbMapPane,_46,_48);
};
this.addFirstListener("addSLD",this.addSLD,this);
this.deleteLayer=function(_49,_4a){
var _4b=_49.getLayer(_4a);
if(!_4b){
alert(mbGetMessage("nodeNotFound",_4a));
return;
}
_4b.parentNode.removeChild(_4b);
_49.modified=true;
};
this.addFirstListener("deleteLayer",this.deleteLayer,this);
this.moveLayerUp=function(_4c,_4d){
var _4e=_4c.getLayer(_4d);
var _4f=_4e.selectSingleNode("following-sibling::*");
if(!_4f){
alert(mbGetMessage("cantMoveUp",_4d));
return;
}
_4e.parentNode.insertBefore(_4f,_4e);
_4c.modified=true;
};
this.addFirstListener("moveLayerUp",this.moveLayerUp,this);
this.moveLayerDown=function(_50,_51){
var _52=_50.getLayer(_51);
var _53=_52.selectNodes("preceding-sibling::*");
var _54=_53[_53.length-1];
if(!_54){
alert(mbGetMessage("cantMoveDown",_51));
return;
}
_52.parentNode.insertBefore(_52,_54);
_50.modified=true;
};
this.addFirstListener("moveLayerDown",this.moveLayerDown,this);
this.setExtension=function(_55){
var _56=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/wmc:Extension");
if(!_56){
var _57=this.doc.selectSingleNode("/wmc:OWSContext/wmc:General");
var _58=createElementWithNS(this.doc,"Extension","http://www.opengis.net/context");
_56=_57.appendChild(_58);
}
return _56.appendChild(_55);
};
this.getExtension=function(){
return this.doc.selectSingleNode("/wmc:OWSContext/wmc:General/wmc:Extension");
};
this.setOpacity=function(_59,_5a){
var _5b=this.getLayer(_59);
if(_5b){
_5b.setAttribute("opacity",_5a);
}
this.callListeners("opacity",_59);
};
this.getOpacity=function(_5c){
var _5d=1;
var _5e=this.getLayer(_5c);
if(_5e){
_5d=_5e.getAttribute("opacity");
}
return _5d;
};
this.initTimeExtent=function(_5f){
var _60=_5f.doc.selectNodes("//wmc:Dimension[@name='time']");
for(var i=0;i<_60.length;++i){
var _62=_60[i];
_5f.timestampList=createElementWithNS(_5f.doc,"TimestampList",mbNsUrl);
var _63;
var _64=_62.parentNode.parentNode;
if(_64.selectSingleNode("@id")){
_63=Mapbuilder.getProperty(_64,"@id");
}else{
_63=Mapbuilder.getProperty(_64,"wmc:Name");
}
_5f.timestampList.setAttribute("layerId",_63);
var _65=getNodeValue(_62).split(",");
for(var j=0;j<_65.length;++j){
var _67=_65[j].split("/");
if(_67.length==3){
var _68=setISODate(_67[0]);
var _69=setISODate(_67[1]);
var _6a=_67[2];
var _6b=_6a.match(/^P((\d*)Y)?((\d*)M)?((\d*)D)?T?((\d*)H)?((\d*)M)?((.*)S)?/);
for(var i=1;i<_6b.length;++i){
if(!_6b[i]){
_6b[i]=0;
}
}
do{
var _6c=createElementWithNS(_5f.doc,"Timestamp",mbNsUrl);
_6c.appendChild(_5f.doc.createTextNode(getISODate(_68)));
_5f.timestampList.appendChild(_6c);
_68.setFullYear(_68.getFullYear()+parseInt(_6b[2],10));
_68.setMonth(_68.getMonth()+parseInt(_6b[4],10));
_68.setDate(_68.getDate()+parseInt(_6b[6],10));
_68.setHours(_68.getHours()+parseInt(_6b[8],10));
_68.setMinutes(_68.getMinutes()+parseInt(_6b[10],10));
_68.setSeconds(_68.getSeconds()+parseFloat(_6b[12]));
}while(_68.getTime()<=_69.getTime());
}else{
var _6c=createElementWithNS(_5f.doc,"Timestamp",mbNsUrl);
_6c.appendChild(_5f.doc.createTextNode(_65[j]));
_5f.timestampList.appendChild(_6c);
}
}
_5f.setExtension(_5f.timestampList);
}
};
this.addFirstListener("loadModel",this.initTimeExtent,this);
this.clearTimeExtent=function(_6d){
var _6e=_6d.timestampList;
if(_6e){
_6e.parentNode.removeChild(_6e);
}
};
this.addListener("newModel",this.clearTimeExtent,this);
this.getCurrentTimestamp=function(_6f){
var _70=this.getParam("timestamp");
return getNodeValue(this.timestampList.childNodes[_70]);
};
}

mapbuilder.loadScript(baseDir+"/model/ModelBase.js");
function Transaction(_1,_2){
ModelBase.apply(this,new Array(_1,_2));
this.namespace="xmlns:gml='http://www.opengis.net/gml' xmlns:wfs='http://www.opengis.net/wfs'";
}

mapbuilder.loadScript(baseDir+"/model/ModelBase.js");
function FeatureCollection(_1,_2){
ModelBase.apply(this,new Array(_1,_2));
this.featureTagName=this.getProperty("mb:featureTagName","topp:CITY_NAME");
this.coordsTagName=this.getProperty("mb:coordsTagName","//gml:coordinates");
this.nodeSelectXpath=this.getProperty("mb:nodeSelectXpath");
this.coordSelectXpath=this.getProperty("mb:coordSelectXpath","topp:the_geom/gml:MultiPoint/gml:pointMember/gml:Point/gml:coordinates");
this.convertCoords=function(_3){
if(_3.doc&&_3.containerModel&&_3.containerModel.doc){
var _4=_3.doc.selectNodes(_3.coordsTagName);
if(_4.length>0&&_3.containerModel){
var _5=_4[0].selectSingleNode("ancestor-or-self::*/@srsName");
if(_5&&(_5.nodeValue.toUpperCase()!=_3.containerModel.getSRS().toUpperCase())){
var _6=new OpenLayers.Projection(_5.nodeValue);
_3.setParam("modelStatus",mbGetMessage("convertingCoords"));
var _7=new OpenLayers.Projection(_3.containerModel.getSRS());
for(var i=0;i<_4.length;++i){
var _9=getNodeValue(_4[i]);
var _a=_9.split(" ");
var _b="";
for(var j=0;j<_a.length;++j){
var xy=_a[j].split(",");
if(xy.length==2){
var pt=new OpenLayers.Geometry.Point(xy[0],xy[1]);
pt.transform(_6,_7);
_b+=pt.x+","+pt.y+" ";
}
}
_4[i].firstChild.nodeValue=_b;
}
}
}
}
};
this.loadWfsRequests=function(_f){
if(_f.containerModel.doc!=null){
var _10=_f.containerModel.doc.selectNodes("/wmc:OWSContext/wmc:ResourceList/wmc:FeatureType");
if(_10.length>0){
for(var i=0;i<_10.length;i++){
var _12=new Object();
var _13=_10[i];
var _14=_13.selectSingleNode("wmc:Server");
var _15=_14.selectSingleNode("wmc:OnlineResource");
_12.method=_15.getAttribute("method");
_12.url=_15.getAttribute("xlink:href");
var _16=_13.selectSingleNode("wfs:GetFeature");
_12.postData=(new XMLSerializer()).serializeToString(_16);
_f.wfsFeature=_13;
_f.newRequest(_f,_12);
}
}
}
};
this.addFirstListener("loadModel",this.convertCoords,this);
if(this.containerModel){
this.containerModel.addListener("loadModel",this.loadWfsRequests,this);
}
this.setHidden=function(_17,_18){
this.hidden=_18;
this.callListeners("hidden",_17);
};
this.getHidden=function(_19){
return this.hidden;
};
this.hidden=false;
this.getFeatureNodes=function(){
return this.doc.selectNodes(this.nodeSelectXpath);
};
this.getFeatureName=function(_1a){
var _1b=_1a.selectSingleNode(this.featureTagName);
return _1b?getNodeValue(_1b):mbGetMessage("noRssTitle");
};
this.getFeatureId=function(_1c){
return _1c.getAttribute("fid")?_1c.getAttribute("fid"):"no_id";
};
this.getFeaturePoint=function(_1d){
var _1e=_1d.selectSingleNode(this.coordSelectXpath);
if(_1e){
var _1f=getNodeValue(_1e).split(",");
return _1f;
}else{
return new Array(0,0);
}
};
this.getFeatureGeometry=function(_20){
var _21=_20.selectSingleNode(this.coordsTagName);
if(_21!=null){
return _21.firstChild;
}else{
alert(mbGetMessage("invalidGeom",(new XMLSerializer()).serializeToString(_20)));
}
};
}

mapbuilder.loadScript(baseDir+"/model/ModelBase.js");
function StyledLayerDescriptor(_1,_2){
ModelBase.apply(this,new Array(_1,_2));
this.sld=null;
this.namespace="xmlns:sld='http://www.opengis.net/sld' xmlns:mb='http://mapbuilder.sourceforge.net/mapbuilder' xmlns:wmc='http://www.opengis.net/context' xmlns:wms='http://www.opengis.net/wms' xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xmlns:ogc='http://www.opengis.net/ogc' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:wfs='http://www.opengis.net/wfs'";
this.sldXPath=this.getProperty("mb:sldXPath","/sld:StyledLayerDescriptor");
this.getSldNode=function(){
return this.doc.selectSingleNode(this.sldXPath);
};
this.loadSLD=function(_3){
var _4=new OpenLayers.Format.SLD();
var _5=_3.doc.selectSingleNode(_3.sldXPath);
_3.sld=_4.read(new XMLSerializer().serializeToString(_5));
};
this.addFirstListener("loadModel",this.loadSLD,this);
}

mapbuilder.loadScript(baseDir+"/model/ModelBase.js");
function WfsCapabilities(_1,_2){
ModelBase.apply(this,new Array(_1,_2));
this.namespace="xmlns:wfs='http://www.opengis.net/wfs'";
this.getServerUrl=function(_3,_4,_5){
var _6="/wfs:WFS_Capabilities/wfs:Capability/wfs:Request/"+_3;
if(_4.toLowerCase()=="post"){
_6+="/wfs:DCPType/wfs:HTTP/wfs:Post";
}else{
_6+="/wfs:DCPType/wfs:HTTP/wfs:Get";
}
return this.doc.selectSingleNode(_6).getAttribute("onlineResource");
};
this.getVersion=function(){
var _7="/wfs:WFS_Capabilities";
return this.doc.selectSingleNode(_7).getAttribute("version");
};
this.getMethod=function(){
return this.method;
};
this.getFeatureNode=function(_8){
return this.doc.selectSingleNode(this.nodeSelectXpath+"[wfs:Name='"+_8+"']");
};
}

mapbuilder.loadScript(baseDir+"/model/ModelBase.js");
function WmsCapabilities(_1,_2){
ModelBase.apply(this,new Array(_1,_2));
this.namespace="xmlns:wms='http://www.opengis.net/wms' xmlns:xlink='http://www.w3.org/1999/xlink'";
this.getServerUrl=function(_3,_4,_5){
var _6=this.getVersion();
if(_6=="1.0.0"){
_3=_3.substring(3);
var _7="/WMT_MS_Capabilities/Capability/Request/"+_3;
if(_4.toLowerCase()=="post"){
_7+="/DCPType/HTTP/Post";
}else{
_7+="/DCPType/HTTP/Get";
}
return this.doc.selectSingleNode(_7).getAttribute("onlineResource");
}else{
var _7="/WMT_MS_Capabilities/Capability/Request/"+_3;
if(_4.toLowerCase()=="post"){
_7+="/DCPType/HTTP/Post/OnlineResource";
}else{
_7+="/DCPType/HTTP/Get/OnlineResource";
}
return this.doc.selectSingleNode(_7).getAttribute("xlink:href");
}
};
this.getVersion=function(){
var _8="/WMT_MS_Capabilities";
return this.doc.selectSingleNode(_8).getAttribute("version");
};
this.getServerTitle=function(){
var _9="/WMT_MS_Capabilities/Service/Title";
return Mapbuilder.getProperty(this.doc,_9,"no title");
};
this.getImageFormat=function(){
var _a=this.getVersion();
if(_a=="1.0.0"){
var _b="/WMT_MS_Capabilities/Capability/Request/Map/Format";
var _c=this.doc.selectSingleNode(_b);
if(_SARISSA_IS_IE){
return "image/"+_c.firstChild.baseName.toLowerCase();
}else{
return "image/"+_c.firstChild.localName.toLowerCase();
}
}else{
var _b="/WMT_MS_Capabilities/Capability/Request/GetMap/Format";
return Mapbuilder.getProperty(this.doc,_b);
}
};
this.getServiceName=function(){
var _d="/WMT_MS_Capabilities/Service/Name";
return Mapbuilder.getProperty(this.doc,_d);
};
this.getFeatureNode=function(_e){
return this.doc.selectSingleNode(this.nodeSelectXpath+"[Name='"+_e+"']");
};
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBase.js");
function Loading(_1,_2){
WidgetBase.apply(this,new Array(_1,_2));
this.paint=function(_3){
var _4=document.getElementById(_3.htmlTagId);
if(_4){
while(_4.childNodes.length>0){
_4.removeChild(_4.childNodes[0]);
}
}
};
this.model.addListener("refresh",this.paint,this);
}

function WidgetBase(_1,_2){
this.model=_2;
this.widgetNode=_1;
var _3=false;
if(_2.modelNode.attributes.getNamedItem("createByTemplate")&&_2.modelNode.attributes.getNamedItem("createByTemplate").nodeValue=="true"){
_1.setAttribute("id","MbWidget_"+mbIds.getId());
_3=true;
}
if(_1.attributes.getNamedItem("id")){
this.id=_1.attributes.getNamedItem("id").nodeValue;
}else{
alert(mbGetMessage("idRequired",_1.nodeName));
}
this.getProperty=function(_4,_5){
return Mapbuilder.getProperty(_1,_4,_5);
};
if(_3){
this.outputNodeId=this.id;
}else{
this.outputNodeId=this.getProperty("mb:outputNodeId","MbWidget_"+mbIds.getId());
}
if(!this.htmlTagId){
this.htmlTagId=this.getProperty("mb:htmlTagId",this.id);
}
this.getNode=function(){
var _6=document.getElementById(this.htmlTagId);
if(!_6){
}
return _6;
};
this.autoRefresh=Mapbuilder.parseBoolean(this.getProperty("mb:autoRefresh",true));
this.debug=Mapbuilder.parseBoolean(this.getProperty("mb:debug",false));
this.initTargetModel=function(_7){
var _8=_7.getProperty("mb:targetModel");
if(_8){
_7.targetModel=window.config.objects[_8];
if(!_7.targetModel){
alert(mbGetMessage("noTargetModelWidget",_8,_7.id));
}
}else{
_7.targetModel=_7.model;
}
};
this.model.addListener("init",this.initTargetModel,this);
this.prePaint=function(_9){
};
this.postPaint=function(_a){
};
this.clearWidget=function(_b){
var _c=document.getElementById(_b.outputNodeId);
var _d=_b.getNode();
if(_d&&_c){
_d.removeChild(_c);
}
};
this.model.addListener("newModel",this.clearWidget,this);
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBase.js");
function WidgetBaseXSL(_1,_2){
WidgetBase.apply(this,new Array(_1,_2));
if(!this.stylesheet){
this.stylesheet=new XslProcessor(this.getProperty("mb:stylesheet",baseDir+"/widget/"+_1.nodeName+".xsl",_2.namespace));
}
this.parseHTMLNodes=Mapbuilder.parseBoolean(this.getProperty("mb:parseHTMLNodes",false));
if(config.widgetText){
var _3="/mb:WidgetText/mb:widgets/mb:"+_1.nodeName;
var _4=config.widgetText.selectNodes(_3+"/*");
for(var j=0;j<_4.length;j++){
this.stylesheet.setParameter(_4[j].nodeName,getNodeValue(_4[j]));
}
}
for(var j=0;j<_1.childNodes.length;j++){
if(_1.childNodes[j].firstChild){
this.stylesheet.setParameter(_1.childNodes[j].nodeName,_1.childNodes[j].firstChild.nodeValue);
}
}
this.stylesheet.setParameter("modelId",this.model.id);
this.stylesheet.setParameter("modelTitle",this.model.title);
this.stylesheet.setParameter("widgetId",this.id);
this.stylesheet.setParameter("skinDir",config.skinDir);
this.stylesheet.setParameter("lang",config.lang);
this.paint=function(_6,_7){
if(_7&&(_7!=_6.id)){
return;
}
if(_6.model.doc&&_6.getNode()){
_6.stylesheet.setParameter("modelUrl",_6.model.url);
_6.stylesheet.setParameter("targetModelId",_6.targetModel.id);
_6.resultDoc=_6.model.doc;
_6.model.setParam("prePaint",_6);
_6.prePaint(_6);
if(_6.debug){
mbDebugMessage(_6,"prepaint:"+(new XMLSerializer()).serializeToString(_6.resultDoc));
}
if(_6.debug){
mbDebugMessage(_6,"stylesheet:"+(new XMLSerializer()).serializeToString(_6.stylesheet.xslDom));
}
var _8=document.getElementById(_6.outputNodeId);
var _9=document.createElement("DIV");
var s=_6.stylesheet.transformNodeToString(_6.resultDoc);
if(config.serializeUrl&&_6.debug){
postLoad(config.serializeUrl,s);
}
if(_6.debug){
mbDebugMessage(_6,"painting:"+_6.id+":"+s);
}
_9.innerHTML=_6.parseHTMLNodes?Sarissa.unescape(s):s;
if(_9.firstChild!=null){
_9.firstChild.setAttribute("id",_6.outputNodeId);
if(_8){
_6.getNode().replaceChild(_9.firstChild,_8);
}else{
_6.getNode().appendChild(_9.firstChild);
}
}
_6.postPaint(_6);
_6.model.setParam("postPaint",_6);
}
};
this.model.addListener("refresh",this.paint,this);
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function TipWidgetBase(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.width=this.getProperty("mb:width",200);
this.height=this.getProperty("mb:height",150);
this.opacity=this.getProperty("mb:opacity",1);
this.backgroundColor=this.getProperty("mb:backgroundColor","D0D0D0");
this.border=this.getProperty("mb:border","0px");
this.config=new Object({model:_2,stylesheet:this.stylesheet,width:this.width,height:this.height,opacity:this.opacity,backgroundColor:this.backgroundColor,border:this.border});
}

mapbuilder.loadScript(baseDir+"/widget/TipWidgetBase.js");
function TipWidgetConfig(_1,_2){
TipWidgetBase.apply(this,new Array(_1,_2));
var _3=this.getProperty("mb:targetWidget");
this.init=function(_4){
if(_3){
if(!_2.config){
_2.config=new Array();
}
_2.config[_3]=_4.config;
}
};
_2.addListener("init",this.init,this);
}

mapbuilder.loadScript(baseDir+"/widget/TipWidgetBase.js");
mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
function TipWidgetOL(_1,_2){
TipWidgetBase.apply(this,new Array(_1,_2));
this.onClick=function(_3){
var _4=_3.model.getParam("olFeatureSelect");
var _5=_3.createPopup(_3,_4,false);
_4.feature.layer.mbClickPopup=_5;
};
this.onMouseover=function(_6){
var _7=_6.model.getParam("olFeatureHover");
if(_7.feature&&!_7.feature.layer.mbClickPopup||!_7.feature.layer.mbClickPopup.visible()){
var _8=_6.createPopup(_6,_7,true);
_7.feature.layer.mbHoverPopup=_8;
_8.events.register("mouseover",_8,_8.hide);
}
};
this.onMouseout=function(_9){
var _a=_9.model.getParam("olFeatureOut");
if(_a&&_a.layer&&_a.layer.mbHoverPopup){
_a.layer.mbHoverPopup.destroy();
_a.layer.mbHoverPopup=null;
}
};
this.createPopup=function(_b,_c,_d){
var _e=_c.feature;
var _f=_b.model.doc.selectSingleNode("//*[@fid='"+_e.fid+"']");
var _10=null;
if(_f){
_10=_f.getAttribute("sourceModel");
}
var _11=null;
if(_10&&config.objects[_10].config&&config.objects[_10].config[_b.id]){
_11=config.objects[_10].config[_b.id];
}else{
_11=_b.config;
}
_11.stylesheet.setParameter("fid",_e.fid);
var _12=_e.layer.map.getLonLatFromPixel(_c.xy);
var _13=new Mapbuilder.Popup(null,_12,new OpenLayers.Size(_11.width,_11.height),new XMLSerializer().serializeToString(_11.stylesheet.transformNodeToObject(_11.model.doc)).replace(/&lt;/g,"<").replace(/&gt;/g,">").replace(/&amp;/g,"&"),null,_d==false);
_13.setOpacity(_11.opacity);
_13.setBackgroundColor(_11.backgroundColor);
_13.setBorder(_11.border);
var _14=_e.layer.map.getExtent().determineQuadrant(_12);
var _15=_14.charAt(1)=="r"?-5:5;
var _16=_14.charAt(0)=="t"?5:-5;
_13.anchor={size:new OpenLayers.Size(0,0),offset:new OpenLayers.Pixel(_15,_16)};
_e.layer.map.addPopup(_13,true);
return _13;
};
}
Mapbuilder.Popup=OpenLayers.Class(OpenLayers.Popup.Anchored,{initialize:function(id,_18,_19,_1a,_1b,_1c,_1d){
OpenLayers.Popup.Anchored.prototype.initialize.apply(this,arguments);
this.contentDiv.style.overflow="hidden";
},setSize:function(_1e){
if(_1e!=undefined){
this.size=_1e;
}
if(this.div!=null){
this.div.style.width=this.size.w+"px";
this.div.style.height=this.size.h+"px";
}
if(this.contentDiv!=null){
this.contentDiv.style.width=this.size.w+"px";
this.contentDiv.style.height=this.size.h+"px";
}
if((this.lonlat)&&(this.map)){
var px=this.map.getLayerPxFromLonLat(this.lonlat);
this.moveTo(px);
}
},addCloseBox:function(_20){
var _21=new OpenLayers.Size(17,17);
var img=config.skinDir+"/openlayers/img/close.gif";
this.closeDiv=OpenLayers.Util.createAlphaImageDiv(this.id+"_close",null,_21,img);
this.closeDiv.style.right=this.padding+"px";
this.closeDiv.style.top=this.padding+"px";
this.groupDiv.appendChild(this.closeDiv);
var _23=_20||function(e){
this.hide();
OpenLayers.Event.stop(e);
};
OpenLayers.Event.observe(this.closeDiv,"click",OpenLayers.Function.bindAsEventListener(_23,this));
},CLASS_NAME:"Mapbuilder.Popup"});

mapbuilder.loadScript(baseDir+"/widget/WidgetBase.js");
function GmlRendererBase(_1,_2){
WidgetBase.apply(this,new Array(_1,_2));
this.featureSRS=this.getProperty("mb:featureSRS");
this.hoverCursorNode=this.getProperty("mb:hoverCursor","pointer");
this.sldModelNode=_1.selectSingleNode("mb:sldModel");
this.defaultStyleName=this.getProperty("mb:defaultStyleName","default");
this.selectStyleName=this.getProperty("mb:selectStyleName","selected");
this.config=new Object({model:_2,hoverCursor:this.hoverCursor,sldModelNode:this.sldModelNode,defaultStyleName:this.defaultStyleName,selectStyleName:this.selectStyleName,featureSRS:this.featureSRS});
}

mapbuilder.loadScript(baseDir+"/widget/GmlRendererBase.js");
function GmlRendererConfig(_1,_2){
GmlRendererBase.apply(this,new Array(_1,_2));
var _3=this.getProperty("mb:targetWidget");
this.init=function(_4){
if(_3){
if(!_2.config){
_2.config={};
}
_2.config[_3]=_4.config;
}
};
_2.addListener("init",this.init,this);
}

mapbuilder.loadScript(baseDir+"/widget/GmlRendererBase.js");
function GmlRendererOL(_1,_2){
GmlRendererBase.apply(this,new Array(_1,_2));
var _3=OpenLayers.Class(OpenLayers.Layer.GML,{loadGML:function(){
if(!this.loaded){
var _4=new OpenLayers.Format.GML();
try{
this.proj=this.projection;
this.addFeatures(_4.read(this.mbWidget.renderDoc));
this.loaded=true;
}
catch(e){
alert(mbGetMessage("documentParseError"),new XMLSerializer().serializeToString(this.mbWidget.renderDoc));
}
}
},calculateInRange:function(){
return true;
},destroyFeatures:function(){
var _5=this.features;
var _6=[];
var _7;
for(var i=0;i<_5.length;i++){
_7=_5[i];
_7.mbWidgetConfig=null;
if(!_7.geometry){
_6.push(_7);
}
}
this.removeFeatures(_6);
for(var i=0;i<_6.length;i++){
_6[i].destroy();
}
OpenLayers.Layer.GML.prototype.destroyFeatures.apply(this,arguments);
},preFeatureInsert:function(_9){
if(_9.geometry){
var _a=this.mbWidget.model.doc.selectSingleNode("//*[@fid='"+_9.fid+"']");
var _b=null;
if(_a){
_b=_a.getAttribute("sourceModel");
}
var _c=null;
if(_b&&config.objects[_b].config&&config.objects[_b].config[this.mbWidget.id]){
_c=config.objects[_b].config[this.mbWidget.id];
}else{
_c=this.mbWidget.config;
}
_9.mbWidgetConfig=_c;
if(!_c.sourceSRS){
if(_c.featureSRS){
_c.sourceSRS=new OpenLayers.Projection(_c.featureSRS);
}else{
_c.sourceSRS=null;
}
}
if(_c.sourceSRS){
this.convertPoints(_9.geometry,_c.sourceSRS);
}
}
},drawFeature:function(_d,_e){
var _f=_d.mbWidgetConfig;
if(_f){
_d.style=null;
if(_f.defaultStyle&&_e!="select"){
_d.style=_f.defaultStyle.createSymbolizer?_f.defaultStyle.createSymbolizer(_d):_f.defaultStyle;
}
if(_f&&_f.selectStyle){
_d.mbSelectStyle=_f.selectStyle;
}
}
OpenLayers.Layer.GML.prototype.drawFeature.apply(this,arguments);
},convertPoints:function(_10,_11){
if(_10.CLASS_NAME=="OpenLayers.Geometry.Point"){
_10.transform(_11,this.proj);
}else{
for(var i=0;i<_10.components.length;++i){
this.convertPoints(_10.components[i],_11);
}
}
},getFeatureByFid:function(fid){
if(!this.features){
return null;
}
for(var i=0;i<this.features.length;++i){
if(this.features[i].fid==fid){
return this.features[i];
}
}
},destroy:function(){
this.mbWidget=null;
OpenLayers.Layer.Vector.prototype.destroy.apply(this,arguments);
}});
this.olLayer=null;
this.defaultStyle=null;
this.selectStyle=null;
this.hiddenFeatures=new Array();
this.containerNodeId=this.htmlTagId;
_2.containerModel=this.targetModel;
if(!this.stylesheet){
var _15=_1.selectSingleNode("mb:stylesheet");
if(_15){
this.stylesheet=new XslProcessor(getNodeValue(_15),_2.namespace);
this.stylesheet.setParameter("proxyUrl",config.proxyUrl);
}
}
this.hoverCursor=this.getProperty("mb:hoverCursor","pointer");
this.paint=function(_16){
if(_16.targetModel.map){
if(_16.olLayer){
_16.model.setParam("gmlRendererLayer",null);
}
_16.renderDoc=_16.stylesheet?_16.stylesheet.transformNodeToObject(_16.model.doc):_16.model.doc;
if(!_16.renderDoc){
return;
}
_16.map=_16.targetModel.map;
var _17=[_16.model];
if(_16.model.mergeModels){
for(var i=0;i<_16.model.mergeModels.length;i++){
_17.push(_16.model.mergeModels[i]);
}
}
for(var i=0;i<_17.length;i++){
var _19=config.objects[_17[i].id].config?config.objects[_17[i].id].config[_16.id]:null;
if(!_19){
_19=_16.config;
}
if(_19.sldModelNode){
var _1a=config.objects[getNodeValue(_19.sldModelNode)];
if(_1a){
_1a.addListener("loadModel",_16.paint,_16);
if(!_1a.doc){
return;
}
var _1b=_1a.getSldNode();
if(_1a.sld){
var _1c=_1a.sld.namedLayers[_16.id].userStyles;
for(var j=0;j<_1c.length;++j){
_1c[j].propertyStyles=_1c[j].findPropertyStyles();
if(_1c[j].name==_19.defaultStyleName){
_19.defaultStyle=_1c[j];
}
if(_1c[j].name==_19.selectStyleName){
_19.selectStyle=_1c[j];
}
}
if(_19.selectStyle){
_19.selectStyle.defaultStyle.cursor=_16.hoverCursor;
}
}else{
if(_1b){
_19.defaultStyle=sld2OlStyle(_1b.selectSingleNode("//*[wmc:Name='"+_19.defaultStyleName+"']"));
_19.selectStyle=sld2OlStyle(_1b.selectSingleNode("//*[wmc:Name='"+_19.selectStyleName+"']"));
if(_19.selectStyle){
_19.selectStyle.cursor=_16.hoverCursor;
}
}
}
}
}
}
if(!_16.olLayer||!_16.olLayer.mbWidget){
_16.olLayer=new _3(_16.id,null,{mbWidget:_16});
_16.targetModel.map.addLayer(_16.olLayer);
}else{
_16.olLayer.loaded=false;
_16.olLayer.destroyFeatures();
_16.olLayer.loadGML();
}
_16.removeHiddenFeatures(_16);
_16.model.setParam("gmlRendererLayer",_16.olLayer);
}
_16.targetModel.addListener("refresh",_16.paint,_16);
};
this.model.addListener("refresh",this.paint,this);
this.hiddenListener=function(_1e,_1f){
alert("hide/unhide "+_1f);
};
this.model.addListener("hidden",this.hiddenListener,this);
this.hideFeature=function(_20,fid){
if(!fid){
fid=_20.model.getParam("hideFeature");
}
var _22=_20.olLayer.getFeatureByFid(fid);
if(_22){
_20.hiddenFeatures.push(fid);
_22.mbHidden=true;
_20.olLayer.renderer.eraseGeometry(_22.geometry);
}
};
this.model.addListener("hideFeature",this.hideFeature,this);
this.showFeature=function(_23,fid){
if(!fid){
fid=_23.model.getParam("showFeature");
}
var _25=_23.olLayer.getFeatureByFid(fid);
if(_25){
OpenLayers.Util.removeItem(_23.hiddenFeatures,fid);
_25.mbHidden=false;
_23.olLayer.drawFeature(_25);
}
};
this.model.addListener("showFeature",this.showFeature,this);
this.removeHiddenFeatures=function(_26){
if(_26.olLayer){
var _27=_26.hiddenFeatures.toString().split(/,/);
_26.hiddenFeatures=new Array();
for(var i=0;i<_27.length;i++){
if(_27[i]){
_26.hideFeature(_26,_27[i]);
}
}
}
};
this.init=function(_29){
var _2a=_1.selectSingleNode("mb:featureOnClick");
if(_2a){
var _2b=config.objects[getNodeValue(_2a)];
_29.model.addListener("olFeatureSelect",_2b.onClick,_2b);
}
var _2c=_1.selectSingleNode("mb:featureOnHover");
if(_2c){
var _2d=config.objects[getNodeValue(_2c)];
_29.model.addListener("olFeatureHover",_2d.onMouseover,_2d);
_29.model.addListener("olFeatureOut",_2d.onMouseout,_2d);
}
_29.targetModel.addListener("aoi",_29.removeHiddenFeatures,_29);
};
this.model.addListener("init",this.init,this);
this.model.removeListener("newModel",this.clearWidget,this);
this.clearWidget=function(_2e){
if(_2e.olLayer){
_2e.olLayer.loaded=false;
for(var i=0;i<_2e.olLayer.map.controls.length;i++){
if(_2e.olLayer.map.controls[i].layer==_2e.olLayer){
_2e.olLayer.map.controls[i].destroy();
}
}
_2e.olLayer.destroy();
_2e.olLayer=null;
}
};
this.model.addListener("newModel",this.clearWidget,this);
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function FeatureList(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.setAttr=function(_3,_4,_5){
_3.model.setXpathValue(_3.model,_4,_5);
};
}

js: Couldn't open file "/home/cameron/tmp/mapbuilder/build/../compressBuild/lib/tool/AoiMouseHandler.js".
mapbuilder.loadScript(baseDir+"/tool/ToolBase.js");
function Caps2Context(_1,_2){
ToolBase.apply(this,new Array(_1,_2));
var _3=baseDir+"/tool/xsl/Caps2Context.xsl";
this.stylesheet=new XslProcessor(_3,_2.namespace);
for(var j=0;j<_1.childNodes.length;j++){
if(getNodeValue(_1.childNodes[j])){
this.stylesheet.setParameter(_1.childNodes[j].nodeName,getNodeValue(_1.childNodes[j]));
}
}
this.mapAllLayers=function(_5){
_5.stylesheet.setParameter("selectedLayer","");
var _6=_5.stylesheet.transformNodeToObject(_5.model.doc);
_5.targetModel.setParam("newModel",null);
_5.targetModel.url="";
_5.targetModel.doc=_6;
_5.targetModel.finishLoading();
};
this.model.addListener("mapAllLayers",this.mapAllLayers,this);
this.mapSingleLayer=function(_7,_8){
_7.stylesheet.setParameter("selectedLayer",_8);
var _9=_7.stylesheet.transformNodeToObject(_7.model.doc);
_7.targetModel.setParam("newModel",null);
_7.targetModel.url="";
_7.targetModel.doc=_9;
_7.targetModel.finishLoading();
};
this.model.addListener("mapLayer",this.mapSingleLayer,this);
}

function ToolBase(_1,_2){
this.model=_2;
this.toolNode=_1;
var id=_1.selectSingleNode("@id");
if(id){
this.id=getNodeValue(id);
}else{
this.id="MbTool_"+mbIds.getId();
}
this.initTargetModel=function(_4){
var _5=_4.toolNode.selectSingleNode("mb:targetModel");
if(_5){
var _6=getNodeValue(_5);
_4.targetModel=window.config.objects[_6];
if(!_4.targetModel){
alert(mbGetMessage("noTargetModelTool",_6,_4.id));
}
}else{
_4.targetModel=_4.model;
}
};
this.model.addListener("init",this.initTargetModel,this);
this.initMouseHandler=function(_7){
var _8=_7.toolNode.selectSingleNode("mb:mouseHandler");
if(_8){
_7.mouseHandler=window.config.objects[getNodeValue(_8)];
if(!_7.mouseHandler){
alert(mbGetMessage("noMouseHandlerTool",getNodeValue(_8),_7.id));
}
}
};
this.model.addListener("init",this.initMouseHandler,this);
this.getProperty=function(_9,_a){
return Mapbuilder.getProperty(_1,_9,_a);
};
this.enabled=Mapbuilder.parseBoolean(this.getProperty("mb:enabled",true));
}

mapbuilder.loadScript(baseDir+"/tool/ToolBase.js");
function EditContext(_1,_2){
ToolBase.apply(this,new Array(_1,_2));
var _3=baseDir+"/tool/xsl/wmc_AddResource.xsl";
this.stylesheet=new XslProcessor(_3);
for(var j=0;j<_1.childNodes.length;j++){
if(getNodeValue(_1.childNodes[j])){
this.stylesheet.setParameter(_1.childNodes[j].nodeName,getNodeValue(_1.childNodes[j]));
}
}
this.addNodeToModel=function(_5){
var _6=this.model.getFeatureNode(_5);
this.stylesheet.setParameter("version",this.model.getVersion());
this.stylesheet.setParameter("serverUrl",this.model.getServerUrl("GetMap","get"));
this.stylesheet.setParameter("serverTitle",this.model.getServerTitle());
this.stylesheet.setParameter("serviceName","wms");
this.stylesheet.setParameter("format",this.model.getImageFormat());
var _7=this.stylesheet.transformNodeToObject(_6);
Sarissa.setXpathNamespaces(_7,this.targetModel.namespace);
mbDebugMessage(this,_7.xml);
this.targetModel.setParam("addLayer",_7.documentElement);
};
this.addLayerFromCat=function(_8){
var _9=this.model.getFeatureNode(_8);
var _a=this.stylesheet.transformNodeToObject(_9);
Sarissa.setXpathNamespaces(_a,this.targetModel.namespace);
mbDebugMessage(this,_a.xml);
this.targetModel.setParam("addLayer",_a.documentElement);
};
this.moveNode=function(_b,_c){
};
this.model.addListener("MoveNode",this.addNodeToModel,this);
this.deleteNode=function(_d,_e){
};
this.model.addListener("DeleteNode",this.addNodeToModel,this);
}

var Rearth=6378137;
var degToMeter=Rearth*2*Math.PI/360;
var mbScaleFactor=3571.428;
var minScale=1000;
var maxScale=200000;
function Extent(_1,_2){
this.model=_1;
this.id=_1.id+"_MbExtent"+mbIds.getId();
this.size=new Array();
this.res=new Array();
this.zoomBy=4;
this.getBbox=function(){
var _3=this.model.getBoundingBox();
return _3;
};
this.setBbox=function(_4){
size=this.getSize();
res=Math.max((_4[2]-_4[0])/size[0],(_4[3]-_4[1])/size[1]);
scale=this.getFixedScale(res);
center=new Array((_4[1]-_4[3])/2,(_4[0]-_4[2])/2);
half=new Array(size[0]/2,size[1]/2);
_4=new Array(center[0]-half[0]*scale,center[1]-half[1]*scale,center[0]+half[0]*scale,center[1]+half[1]*scale);
this.model.setBoundingBox(_4);
};
this.getSize=function(){
size=new Array();
size[0]=this.model.getWindowWidth();
size[1]=this.model.getWindowHeight();
return size;
};
this.setSize=function(_5){
this.model.setWindowWidth(_5[0]);
this.model.setWindowHeight(_5[1]);
};
this.getFixedScale=function(_6){
if(this.zoomLevels){
if(!_6){
this.setResolution(new Array(this.model.getWindowWidth(),this.model.getWindowHeight()));
_6=Math.max(this.res[0],this.res[1]);
}
var _7="function sort(a,b){return b-a}";
var _8=eval(_7);
var _9=this.zoomLevels.sort(_8);
var i=0;
while(_9[i]>=_6){
i++;
}
if(i==0){
i=1;
}
this.fixedScale=_9[i-1];
}else{
this.fixedScale=Math.max(this.res[0],this.res[1]);
}
return this.fixedScale;
};
this.setZoomLevels=function(_b,_c){
if(_b){
this.zoomLevels=_c;
}else{
this.zoomLevels=null;
}
};
this.checkBbox=function(){
var _d=this.getCenter();
var _e=new Array(this.size[0]/2,this.size[1]/2);
var _f=this.getFixedScale();
this.lr=new Array(_d[0]+_e[0]*_f,_d[1]-_e[1]*_f);
this.ul=new Array(_d[0]-_e[0]*_f,_d[1]+_e[1]*_f);
};
this.getCenter=function(){
return new Array((this.ul[0]+this.lr[0])/2,(this.ul[1]+this.lr[1])/2);
};
this.getXY=function(pl){
latlng=new Array(this.ul[0]+pl[0]*this.res[0],this.ul[1]-pl[1]*this.res[1]);
return latlng;
};
this.getPL=function(xy){
var p=Math.floor((xy[0]-this.ul[0])/this.res[0]);
var l=Math.floor((this.ul[1]-xy[1])/this.res[1]);
return new Array(p,l);
};
this.centerAt=function(_14,_15,_16){
var _17=new Array(this.size[0]/2,this.size[1]/2);
if(this.zoomLevels){
_15=this.getFixedScale(_15);
}
this.lr=new Array(_14[0]+_17[0]*_15,_14[1]-_17[1]*_15);
this.ul=new Array(_14[0]-_17[0]*_15,_14[1]+_17[1]*_15);
if(_16){
var _18=0;
if(this.lr[0]>ContextExtent.lr[0]){
_18=ContextExtent.lr[0]-this.lr[0];
}
if(this.ul[0]<ContextExtent.ul[0]){
_18=ContextExtent.ul[0]-this.ul[0];
}
this.lr[0]+=_18;
this.ul[0]+=_18;
var _19=0;
if(this.lr[1]<ContextExtent.lr[1]){
_19=ContextExtent.lr[1]-this.lr[1];
}
if(this.ul[1]>ContextExtent.ul[1]){
_19=ContextExtent.ul[1]-this.ul[1];
}
this.lr[1]+=_19;
this.ul[1]+=_19;
}
this.model.setBoundingBox(new Array(this.ul[0],this.lr[1],this.lr[0],this.ul[1]));
this.setSize(_15);
};
this.zoomToBox=function(ul,lr){
var _1c=new Array((ul[0]+lr[0])/2,(ul[1]+lr[1])/2);
newres=Math.max((lr[0]-ul[0])/this.size[0],(ul[1]-lr[1])/this.size[1]);
this.centerAt(_1c,newres);
};
this.setSize=function(res){
this.res[0]=this.res[1]=res;
this.size[0]=(this.lr[0]-this.ul[0])/this.res[0];
this.size[1]=(this.ul[1]-this.lr[1])/this.res[1];
this.width=Math.ceil(this.size[0]);
this.height=Math.ceil(this.size[1]);
};
this.setResolution=function(_1e){
this.size[0]=_1e[0];
this.size[1]=_1e[1];
this.res[0]=(this.lr[0]-this.ul[0])/this.size[0];
this.res[1]=(this.ul[1]-this.lr[1])/this.size[1];
this.width=Math.ceil(this.size[0]);
this.height=Math.ceil(this.size[1]);
};
this.getScale=function(){
var _1f=null;
switch(this.model.getSRS()){
case "EPSG:GMAPS":
break;
case "EPSG:4326":
case "EPSG:4269":
_1f=this.res[0]*degToMeter;
break;
default:
_1f=this.res[0];
break;
}
return mbScaleFactor*_1f;
};
this.setScale=function(_20){
var _21=null;
switch(this.model.getSRS()){
case "EPSG:4326":
case "EPSG:4269":
_21=_20/(mbScaleFactor*degToMeter);
break;
default:
_21=_20/mbScaleFactor;
break;
}
this.centerAt(this.getCenter(),_21);
};
this.init=function(_22,_23){
var _24=_22.model.getBoundingBox();
_22.ul=new Array(_24[0],_24[3]);
_22.lr=new Array(_24[2],_24[1]);
_22.setResolution(new Array(_22.model.getWindowWidth(),_22.model.getWindowHeight()));
_22.checkBbox();
};
if(_2){
this.init(this,_2);
}
this.firstInit=function(_25,_26){
_25.init(_25,_26);
};
}

mapbuilder.loadScript(baseDir+"/tool/ToolBase.js");
function History(_1,_2){
ToolBase.apply(this,new Array(_1,_2));
this.init=function(_3){
_3.model.active=-1;
_3.model.historyList=new Array();
_3.add(_3);
};
this.add=function(_4){
if(_4.model.active!=null){
var _5=_4.model.active;
var _6=_4.model.historyList;
var _7=_4.targetModel.map.getExtent().getCenterLonLat();
var _8=_4.targetModel.map.getScale()-1;
if(_5>-1){
if(_7.toString()==_6[_5].center.toString()&&_8==_6[_5].scale){
return;
}
}
var _9=new Object({center:_7,scale:_8});
if(_5==(_6.length-1)){
_6.push(_9);
_5=_5+1;
}else{
_5=_5+1;
_6=_6.slice(0,_5);
_6.push(_9);
}
_4.model.active=_5;
_4.model.historyList=_6;
}
};
this.back=function(_a){
var _b=_a.model.active;
if(_b<1){
_a.model.previousExtent=null;
alert(mbGetMessage("cantGoBack"));
}else{
_b=_b-1;
_a.model.active=_b;
_a.model.previousExtent=_a.model.historyList[_b];
}
};
this.forward=function(_c){
var _d=_c.model.active;
if(_d<(_c.model.historyList.length-1)){
_d=_d+1;
_c.model.active=_d;
_c.model.nextExtent=_c.model.historyList[_d];
}else{
_c.model.nextExtent=null;
alert(mbGetMessage("cantGoForward"));
}
};
this.stop=function(_e){
_e.model.removeListener("bbox",_e.add,_e);
};
this.start=function(_f){
_f.model.addListener("bbox",_f.add,_f);
};
this.initReset=function(_10){
_10.targetModel.addListener("bbox",_10.add,_10);
_10.targetModel.addListener("loadModel",_10.init,_10);
};
this.model.addListener("historyBack",this.back,this);
this.model.addListener("historyForward",this.forward,this);
this.model.addListener("historyStart",this.start,this);
this.model.addListener("historyStop",this.stop,this);
this.model.addListener("init",this.initReset,this);
}

mapbuilder.loadScript(baseDir+"/tool/ToolBase.js");
mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
function FeatureSelectHandler(_1,_2){
ToolBase.apply(this,new Array(_1,_2));
this.map=null;
this.sourceModels=new Array();
this.configInit=function(_3){
_3.targetModel.addListener("loadModel",_3.contextInit,_3);
};
this.model.addListener("init",this.configInit,this);
this.clear=function(_4){
if(_4.control){
_4.map=null;
_4.control.destroy();
_4.control=null;
}
};
this.model.addListener("newModel",this.clear,this);
this.contextInit=function(_5){
_5.targetModel.addListener("newModel",_5.clear,_5);
_5.model.addListener("gmlRendererLayer",_5.init,_5);
if(_5.targetModel.map&&_5.model.getParam("gmlRendererLayer")&&!_5.control){
_5.init(_5);
}
};
this.init=function(_6){
var _7;
if(_6.model.mergeModels){
_6.sourceModels=_6.model.mergeModels;
}else{
_6.sourceModels.push(_6.model);
}
for(var i=0;i<_6.sourceModels.length;i++){
_6.sourceModels[i].addListener("highlightFeature",_6.highlight,_6);
_6.sourceModels[i].addListener("dehighlightFeature",_6.dehighlight,_6);
}
var _9=_6.model.getParam("gmlRendererLayer");
if(_6.map==_6.targetModel.map&&_6.control&&!_9){
_6.map.removeControl(_6.control);
_6.control.destroy();
_6.control=null;
}else{
if(_9){
if(!_6.control){
_6.control=new OpenLayers.Control.SelectFeature(_9,{hover:true,onSelect:_6.onSelect,onUnselect:_6.onUnselect,mbFeatureSelectHandler:_6,select:function(_a){
_a.mbFeatureSelectHandler=this.mbFeatureSelectHandler;
if(_a.mbSelectStyle){
this.selectStyle=_a.mbSelectStyle.createSymbolizer?_a.mbSelectStyle.createSymbolizer(_a):_a.mbSelectStyle;
}
OpenLayers.Control.SelectFeature.prototype.select.apply(this,arguments);
}});
_6.map=_6.targetModel.map;
_6.map.addControl(_6.control);
}
_6.control.activate();
}
}
};
var _b=function(){
var _c=this.mbFeatureSelectHandler;
if(this.layer.events&&_c){
this.layer.events.unregister("mousedown",this,_c.onClick);
this.layer.events.unregister("mousemove",this,_c.onHover);
}
this.mbFeatureSelectHandler=null;
OpenLayers.Feature.Vector.prototype.destroy.apply(this,arguments);
};
this.onSelect=function(_d){
if(!_d){
return;
}
var _e=this.mbFeatureSelectHandler;
for(var i=0;i<_e.sourceModels.length;i++){
_e.sourceModels[i].setParam("mouseoverFeature",_d.fid);
}
if(_d.layer.events&&!_d.mbNoMouseEvent){
_d.destroy=_b;
_d.layer.events.registerPriority("mousedown",_d,_e.onClick);
_d.layer.events.registerPriority("mousemove",_d,_e.onHover);
}else{
_d.mbNoMouseEvent=null;
}
};
this.onUnselect=function(_10){
if(!_10){
return;
}
var _11=this.mbFeatureSelectHandler||_10.mbFeatureSelectHandler;
for(var i=0;i<_11.sourceModels.length;i++){
_11.sourceModels[i].setParam("mouseoutFeature",_10.fid);
}
_11.model.setParam("olFeatureOut",_10);
if(_10.layer.events){
_10.layer.events.unregister("mousedown",_10,_11.onClick);
}
};
this.onClick=function(evt){
evt.feature=this;
var _14=this.mbFeatureSelectHandler;
_14.model.setParam("olFeatureSelect",evt);
OpenLayers.Event.stop(evt);
};
this.onHover=function(evt){
var _16=this.mbFeatureSelectHandler;
if(this.layer&&this.layer.events){
this.layer.events.unregister("mousemove",this,_16.onHover);
}
evt.feature=this;
_16.model.setParam("olFeatureHover",evt);
};
this.highlight=function(_17,fid){
var _19,feature;
var _1a=_17.model.getParam("gmlRendererLayer");
for(var i=0;i<_17.sourceModels.length;i++){
_19=_17.sourceModels[i];
if(!_1a){
return;
}
if(!fid){
fid=_19.getParam("highlightFeature");
}
feature=_1a.getFeatureByFid(fid);
if(feature&&!feature.mbHidden){
feature.mbNoMouseEvent=true;
_17.control.select(feature);
}
}
};
this.dehighlight=function(_1c,fid){
var _1e,feature;
var _1f=_1c.model.getParam("gmlRendererLayer");
for(var i=0;i<_1c.sourceModels.length;i++){
_1e=_1c.sourceModels[i];
if(!_1f){
return;
}
if(!fid){
fid=_1c.model.getParam("dehighlightFeature");
}
feature=_1f.getFeatureByFid(fid);
if(feature&&!feature.mbHidden){
_1c.control.unselect(feature);
}
}
};
}

mapbuilder.loadScript(baseDir+"/tool/ToolBase.js");
function MergeModels(_1,_2){
ToolBase.apply(this,new Array(_1,_2));
this.template=null;
this.init=function(_3){
_3.model.mergeModels=new Array();
var _4=_1.selectSingleNode("mb:merges");
if(_4){
var _5=_4.childNodes;
for(var i=0;i<_5.length;i++){
if(_5[i].firstChild){
_3.addModel(_3,config.objects[getNodeValue(_5[i])]);
}
}
}
};
_2.addListener("init",this.init,this);
this.getTemplate=function(_7){
_7.template=_7.model.doc?_7.model.doc.cloneNode(true):null;
if(_7.template){
_7.model.removeListener("loadModel",_7.getTemplate,_7);
_7.buildModel(_7);
}
};
_2.addListener("loadModel",this.getTemplate,this);
this.addModel=function(_8,_9){
_8.model.mergeModels.push(_9);
if(_9.doc){
_8.mergeModel(_8,_9.doc);
}
_9.addListener("refresh",_8.buildModel,_8);
};
this.mergeModel=function(_a,_b){
var _c=_b.doc?_b.doc.cloneNode(true):null;
var _d=_c?_c.selectNodes("//*[@fid]"):null;
if(!_d){
return;
}
var _e;
for(var i=0;i<_d.length;i++){
_e=_d[i];
if(_e.nodeName){
_e.setAttribute("sourceModel",_b.id);
}
}
Sarissa.copyChildNodes(_c.documentElement,_a.model.doc.documentElement,true);
};
this.buildModel=function(_10){
if(!_10.template){
return;
}
_10.model.callListeners("newModel");
_10.model.doc=_10.template.cloneNode(true);
for(var i=0;i<_10.model.mergeModels.length;i++){
_10.mergeModel(_10,_10.model.mergeModels[i]);
}
_10.model.callListeners("loadModel");
};
}

mapbuilder.loadScript(baseDir+"/tool/ToolBase.js");
function MovieLoop(_1,_2){
ToolBase.apply(this,new Array(_1,_2));
this.frameIncrement=1;
this.model.setParam("firstFrame",0);
this.timestampIndex=0;
window.movieLoop=this;
this.isRunning=false;
this.frameIsLoading=false;
this.delay=1000/this.getProperty("mb:framesPerSecond",10);
this.maxFrames=this.getProperty("mb:maxFrames",30);
this.setFrame=function(_3){
var _4=this.model.timestampList;
var ts;
if(this.timestampIndex!=null){
var ts=_4.childNodes[this.timestampIndex];
if(ts){
ts.setAttribute("current","0");
this.model.setParam("timestamp",this.timestampIndex);
}
}
var _6=this.model.getParam("firstFrame");
var _7=this.model.getParam("lastFrame");
if(_3>_7){
_3=_6;
}
if(_3<_6){
_3=_7;
}
this.timestampIndex=_3;
ts=_4.childNodes[this.timestampIndex];
ts.setAttribute("current","1");
this.model.setParam("timestamp",this.timestampIndex);
};
this.nextFrame=function(_8){
var _9=window.movieLoop;
var _a=_9.frameIncrement;
if(_8){
_a=_8;
}
if(!this.frameIsLoading){
_9.setFrame(_9.timestampIndex+_a);
}
};
this.setFrameLimits=function(_b){
var _c=_b.model.timestampList;
var _d=_b.model.getParam("firstFrame");
var _e=_d+_b.maxFrames;
if(_e>_c.childNodes.length){
_e=_c.childNodes.length-1;
}
_b.model.setParam("lastFrame",_e);
_c.childNodes[_d].setAttribute("current","1");
};
this.model.addFirstListener("refresh",this.setFrameLimits,this);
this.model.addListener("firstFrame",this.setFrameLimits,this);
this.reset=function(_f){
_f.pause();
_f.setFrame(_f.model.getParam("firstFrame"));
};
this.model.addListener("loadModel",this.reset,this);
this.init=function(_10){
if(!_10.initialized){
_10.initialized=true;
_10.reset(_10);
}
};
this.model.addListener("bbox",this.init,this);
this.uninit=function(_11){
_11.initialized=false;
};
this.model.addListener("newModel",this.uninit,this);
this.play=function(){
if(!this.isRunning){
this.movieTimer=setInterval("window.movieLoop.nextFrame()",this.delay);
this.isRunning=true;
}
};
this.pause=function(){
this.isRunning=false;
clearInterval(this.movieTimer);
};
this.stop=function(){
this.pause();
this.reset(this);
};
this.stopListener=function(_12){
_12.stop();
};
this.model.addListener("stopLoop",this.stopListener,this);
}

mapbuilder.loadScript(baseDir+"/tool/ToolBase.js");
function WebServiceRequest(_1,_2){
ToolBase.apply(this,new Array(_1,_2));
this.requestName=this.getProperty("mb:requestName");
this.requestFilter=this.getProperty("mb:requestFilter");
var _3=_1.selectSingleNode("mb:stylesheet");
_3=_3?getNodeValue(_3):baseDir+"/tool/xsl/"+this.requestName.replace(/:/,"_")+".xsl";
this.requestStylesheet=new XslProcessor(_3);
for(var j=0;j<_1.childNodes.length;j++){
if(getNodeValue(_1.childNodes[j])){
this.requestStylesheet.setParameter(_1.childNodes[j].nodeName,getNodeValue(_1.childNodes[j]));
}
}
this.model.addListener("init",this.init,this);
this.model.addListener(this.requestName.replace(/:/,"_"),this.doRequest,this);
}
WebServiceRequest.prototype.createHttpPayload=function(_5){
if(this.debug){
mbDebugMessage(this,"source:"+(new XMLSerializer()).serializeToString(_5));
}
var _6=new Object();
_6.method=this.targetModel.method;
this.requestStylesheet.setParameter("httpMethod",_6.method);
this.requestStylesheet.setParameter("version",this.model.getVersion(_5));
if(this.requestFilter){
var _7=config.objects[this.requestFilter];
this.requestStylesheet.setParameter("filter",escape((new XMLSerializer()).serializeToString(_7.doc).replace(/[\n\f\r\t]/g,"")));
if(this.debug){
mbDebugMessage(this,(new XMLSerializer()).serializeToString(_7.doc));
}
}
_6.postData=this.requestStylesheet.transformNodeToObject(_5);
if(this.debug){
mbDebugMessage(this,"request data:"+(new XMLSerializer()).serializeToString(_6.postData));
if(config.serializeUrl){
var _8=postLoad(config.serializeUrl,_6.postData);
}
}
_6.url=this.model.getServerUrl(this.requestName,_6.method,_5);
if(_6.method.toLowerCase()=="get"){
_6.postData.setProperty("SelectionLanguage","XPath");
Sarissa.setXpathNamespaces(_6.postData,"xmlns:mb='http://mapbuilder.sourceforge.net/mapbuilder'");
var _9=_6.postData.selectSingleNode("//mb:QueryString");
if(_6.url.indexOf("?")<0){
_6.url+="?";
}else{
_6.url+="&";
}
_6.url+=getNodeValue(_9);
_6.postData=null;
}
mbDebugMessage(this,"URL:"+_6.url);
return _6;
};
WebServiceRequest.prototype.doRequest=function(_a,_b){
_a.targetModel.featureName=_b;
var _c=_a.model.getFeatureNode(_b);
if(!_c){
alert(mbGetMessage("featureNotFoundWebServiceRequest",_b));
return;
}
if(_a.model.setRequestParameters){
_a.model.setRequestParameters(_b,_a.requestStylesheet);
}
var _d=_a.createHttpPayload(_c);
_a.targetModel.newRequest(_a.targetModel,_d);
};
WebServiceRequest.prototype.setAoiParameters=function(_e){
if(_e.containerModel){
var _f=null;
var _10="EPSG:4326";
var _11=_e.containerModel.getBoundingBox();
var _10=_e.containerModel.getSRS();
_e.requestStylesheet.setParameter("bBoxMinX",_11[0]);
_e.requestStylesheet.setParameter("bBoxMinY",_11[1]);
_e.requestStylesheet.setParameter("bBoxMaxX",_11[2]);
_e.requestStylesheet.setParameter("bBoxMaxY",_11[3]);
_e.requestStylesheet.setParameter("srs",_10);
_e.requestStylesheet.setParameter("width",_e.containerModel.getWindowWidth());
_e.requestStylesheet.setParameter("height",_e.containerModel.getWindowHeight());
}
};
WebServiceRequest.prototype.init=function(_12){
if(_12.targetModel.containerModel){
_12.containerModel=_12.targetModel.containerModel;
}else{
if(_12.model.containerModel){
_12.containerModel=_12.model.containerModel;
}
}
if(_12.containerModel){
_12.containerModel.addListener("aoi",_12.setAoiParameters,_12);
_12.containerModel.addListener("bbox",_12.setAoiParameters,_12);
_12.containerModel.addListener("selectedLayer",_12.selectFeature,_12);
_12.containerModel.addListener("loadModel",_12.mapInit,_12);
_12.containerModel.addListener("newModel",_12.clear,_12);
}
};
WebServiceRequest.prototype.mapInit=function(_13){
_13.containerModel.map.events.registerPriority("mouseup",_13,_13.setClickPosition);
};
WebServiceRequest.prototype.clear=function(_14){
if(_14.containerModel.map&&_14.containerModel.map.events){
_14.containerModel.map.events.unregister("mouseup",_14,_14.setClickPosition);
}
};
WebServiceRequest.prototype.setClickPosition=function(e){
this.targetModel.deleteTemplates();
this.requestStylesheet.setParameter("xCoord",e.xy.x);
this.requestStylesheet.setParameter("yCoord",e.xy.y);
};
WebServiceRequest.prototype.selectFeature=function(_16,_17){
_16.requestStylesheet.setParameter("queryLayer",_17);
};

mapbuilder.loadScript(baseDir+"/tool/ToolBase.js");
function ZoomToAoi(_1,_2){
ToolBase.apply(this,new Array(_1,_2));
this.initProj=function(_3){
if(_3.model.doc&&_3.targetModel.doc){
if(_3.model.getSRS()!=_3.targetModel.getSRS()){
_3.model.proj=new OpenLayers.Projection(_3.model.getSRS());
_3.targetModel.proj=new OpenLayers.Projection(_3.targetModel.getSRS());
}
}
};
this.setListeners=function(_4){
_4.model.addListener("loadModel",_4.initProj,_4);
_4.targetModel.addListener("loadModel",_4.initProj,_4);
_4.initProj(_4);
};
this.model.addListener("loadModel",this.setListeners,this);
this.showTargetAoi=function(_5){
if(_5.targetModel.doc){
var _6=_5.targetModel.getBoundingBox();
var ul=new Array(_6[0],_6[3]);
var lr=new Array(_6[2],_6[1]);
if(_5.model.getSRS()!=_5.targetModel.getSRS()){
var _9=new OpenLayers.Geometry.Point(ul[0],ul[1]);
var _a=new OpenLayers.Geometry.Point(lr[0],lr[1]);
_9.transform(_5.targetModel.proj,_5.model.proj);
_a.transform(_5.targetModel.proj,_5.model.proj);
ul=new Array(_9.x,_9.y);
lr=new Array(_a.x,_a.y);
}
_5.model.setParam("aoi",new Array(ul,lr));
}
};
this.firstInit=function(_b){
_b.model.map.events.register("mouseup",_b,_b.mouseUpHandler);
_b.targetModel.addListener("loadModel",_b.showTargetAoi,_b);
_b.targetModel.addListener("bbox",_b.showTargetAoi,_b);
_b.showTargetAoi(_b);
};
this.model.addListener("loadModel",this.firstInit,this);
this.clear=function(_c){
if(_c.model.map&&_c.model.map.events){
_c.model.map.events.unregister("mouseup",_c,_c.mouseUpHandler);
}
};
this.model.addListener("clearModel",this.clear,this);
}
ZoomToAoi.prototype.mouseUpHandler=function(e){
var _e=this.model.getParam("aoi");
var ul=_e[0];
var lr=_e[1];
if(this.model.getSRS()!=this.targetModel.getSRS()){
var _11=new OpenLayers.Geometry.Point(ul[0],ul[1]);
var _12=new OpenLayers.Geometry.Point(lr[0],lr[1]);
_11.transform(this.model.proj,this.targetModel.proj);
_12.transform(this.model.proj,this.targetModel.proj);
ul=new Array(_11.x,_11.y);
lr=new Array(_12.x,_12.y);
}
if((ul[0]==lr[0])&&(ul[1]==lr[1])){
this.targetModel.map.setCenter(new OpenLayers.LonLat(ul[0],ul[1]));
}else{
this.targetModel.map.zoomToExtent(new OpenLayers.Bounds(ul[0],lr[1],lr[0],ul[1]));
}
};

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function GetFeatureInfoWSR(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.controller=this.getProperty("mb:controller");
this.tolerance=parseFloat(this.getProperty("mb:tolerance",3));
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{CLASS_NAME:"mbControl.GetFeatureInfoWSR",type:OpenLayers.Control.TYPE_TOOL});
return _4;
};
this.doSelect=function(_5,_6){
if(_6){
_5.targetModel.map.events.register("click",_5,_5.doOnClick);
}else{
_5.targetModel.map.events.unregister("click",_5,_5.doOnClick);
}
};
this.doOnClick=function(e){
objRef=this;
if(!objRef.enabled){
return;
}
var _8=config.objects[objRef.controller];
var _9=new Array();
var _a=objRef.targetModel.getParam("selectedLayer");
var _b;
if(!_a){
_b=objRef.targetModel.getQueryableLayers();
if(_b.length==0){
alert(mbGetMessage("noQueryableLayers"));
return;
}
}else{
_b=[objRef.targetModel.getLayer(_a)];
}
var _c=e.xy.add(-objRef.tolerance,objRef.tolerance);
var _d=e.xy.add(objRef.tolerance,-objRef.tolerance);
var ll=objRef.targetModel.map.getLonLatFromPixel(_c);
var ur=objRef.targetModel.map.getLonLatFromPixel(_d);
for(var i=0;i<_b.length;++i){
var _11=_b[i];
var _12=Mapbuilder.getProperty(_11,"wmc:Name","");
var _13=objRef.targetModel.getHidden(_12);
if(_13==0){
_8.requestStylesheet.setParameter("bBoxMinX",ll.lon);
_8.requestStylesheet.setParameter("bBoxMinY",ll.lat);
_8.requestStylesheet.setParameter("bBoxMaxX",ur.lon);
_8.requestStylesheet.setParameter("bBoxMaxY",ur.lat);
_8.requestStylesheet.setParameter("queryLayer",_12);
objRef.targetModel.setParam(_8.requestName.replace(/:/,"_"),_12);
}
}
};
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function Graticule(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.display=false;
this.color=this.getProperty("mb:color");
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{CLASS_NAME:"mbControl.Graticule",type:OpenLayers.Control.TYPE_TOGGLE,destroy:function(){
OpenLayers.Control.prototype.destroy.apply(this,arguments);
if(this.divs){
for(i=this.divs.length;i>0;--i){
this.divs[i]=null;
}
}
this.div=null;
this.mapContainer=null;
},removeGraticules:function(){
try{
var i=0;
var _6=this.mapContainer;
if(this.divs){
for(i=0;i<this.divs.length;i++){
_6.removeChild(this.divs[i]);
}
}
}
catch(e){
}
},getBbox:function(){
var _7=new Object();
_7.ll=new Object();
_7.ur=new Object();
var ll=new OpenLayers.Geometry.Point(this.bbox[0],this.bbox[1]);
var ur=new OpenLayers.Geometry.Point(this.bbox[2],this.bbox[3]);
var _a=new OpenLayers.Projection("EPSG:4326");
ll.transform(this.proj,_a);
ur.transform(this.proj,_a);
_7.ll.lon=ll.x;
_7.ll.lat=ll.y;
_7.ur.lon=ur.x;
_7.ur.lat=ur.y;
return _7;
},gridIntervalMins:function(_b){
var _b=_b/10;
_b*=6000;
_b=Math.ceil(_b)/100;
if(_b<=0.06){
_b=0.06;
}else{
if(_b<=0.12){
_b=0.12;
}else{
if(_b<=0.3){
_b=0.3;
}else{
if(_b<=0.6){
_b=0.6;
}else{
if(_b<=1.2){
_b=1.2;
}else{
if(_b<=3){
_b=3;
}else{
if(_b<=6){
_b=6;
}else{
if(_b<=12){
_b=12;
}else{
if(_b<=30){
_b=30;
}else{
if(_b<=60){
_b=30;
}else{
if(_b<=(60*2)){
_b=60*2;
}else{
if(_b<=(60*5)){
_b=60*5;
}else{
if(_b<=(60*10)){
_b=60*10;
}else{
if(_b<=(60*20)){
_b=60*20;
}else{
if(_b<=(60*30)){
_b=60*30;
}else{
_b=60*45;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
return _b;
},gridPrecision:function(_c){
if(_c<0.01){
return 3;
}else{
if(_c<0.1){
return 2;
}else{
if(_c<1){
return 1;
}else{
return 0;
}
}
}
},addGraticules:function(){
this.removeGraticules();
var _d=this.getBbox();
var l=_d.ll.lon;
var b=_d.ll.lat;
var r=_d.ur.lon;
var t=_d.ur.lat;
if(b<-90){
b=-90;
}
if(t>90){
t=90;
}
if(l<-180){
l=-180;
}
if(r>180){
r=180;
}
if(l==r){
l=-180;
r=180;
}
if(t==b){
b=-90;
t=90;
}
var _12=this.gridIntervalMins(t-b);
var _13;
if(r>l){
_13=this.gridIntervalMins(r-l);
}else{
_13=this.gridIntervalMins((180-l)+(r+180));
}
l=Math.floor(l*60/_13)*_13/60;
b=Math.floor(b*60/_12)*_12/60;
t=Math.ceil(t*60/_12)*_12/60;
r=Math.ceil(r*60/_13)*_13/60;
if(b<=-90){
b=-90;
}
if(t>=90){
t=90;
}
if(l<-180){
l=-180;
}
if(r>180){
r=180;
}
_12/=60;
_13/=60;
this.dLat=_12;
this.dLon=_13;
var _14=this.gridPrecision(_12);
var _15=this.gridPrecision(_13);
this.divs=new Array();
var i=0;
var pbl=this.fromLatLngToDivPixel(b,l);
var ptr=this.fromLatLngToDivPixel(t,r);
this.maxX=ptr.x;
this.maxY=pbl.y;
this.minX=pbl.x;
this.minY=ptr.y;
var x;
var y=this.fromLatLngToDivPixel(b+_12+_12,l).y+2;
var _1b=this.mapContainer;
var lo=l;
if(r<lo){
r+=360;
}
while(lo<=r){
var p=this.fromLatLngToDivPixel(b,lo);
this.divs[i]=this.createVLine(p.x);
this.divs[i].style.zIndex=this.div.style.zIndex;
_1b.insertBefore(this.divs[i],null);
i++;
var d=document.createElement("DIV");
x=p.x+3;
d.style.position="absolute";
d.style.left=x.toString()+"px";
d.style.top=y.toString()+"px";
d.style.color=this.color;
d.style.fontFamily="Arial";
d.style.fontSize="x-small";
if(lo==0){
d.innerHTML=(Math.abs(lo)).toFixed(_15);
}else{
if(lo<0){
d.title=mbGetMessage("westWgs84");
d.innerHTML=(Math.abs(lo)).toFixed(_15)+" W";
}else{
d.title=mbGetMessage("eastWgs84");
d.innerHTML=(Math.abs(lo)).toFixed(_15)+" E";
}
}
d.style.zIndex=this.div.style.zIndex;
_1b.insertBefore(d,null);
this.divs[i]=d;
i++;
lo+=_13;
if(lo>180){
r-=360;
lo-=360;
}
}
var j=0;
x=this.fromLatLngToDivPixel(b,l+_13+_13).x+3;
while(b<=t){
var p=this.fromLatLngToDivPixel(b,l);
if(r<l){
this.divs[i]=this.createHLine3(b);
this.divs[i].style.zIndex=this.div.style.zIndex;
_1b.insertBefore(this.divs[i],null);
i++;
}else{
if(r==l){
this.divs[i]=this.createHLine3(b);
this.divs[i].style.zIndex=this.div.style.zIndex;
_1b.insertBefore(this.divs[i],null);
i++;
}else{
this.divs[i]=this.createHLine(p.y);
this.divs[i].style.zIndex=this.div.style.zIndex;
_1b.insertBefore(this.divs[i],null);
i++;
}
}
var d=document.createElement("DIV");
y=p.y+2;
d.style.position="absolute";
d.style.left=x.toString()+"px";
d.style.top=y.toString()+"px";
d.style.color=this.color;
d.style.fontFamily="Arial";
d.style.fontSize="x-small";
if(b==0){
d.innerHTML=(Math.abs(b)).toFixed(_15);
}else{
if(b<0){
d.title=mbGetMessage("southWgs84");
d.innerHTML=(Math.abs(b)).toFixed(_14)+" S";
}else{
d.title=mbGetMessage("northWgs84");
d.innerHTML=(Math.abs(b)).toFixed(_14)+" N";
}
}
if(j!=2){
d.style.zIndex=this.div.style.zIndex;
_1b.insertBefore(d,null);
this.divs[i]=d;
i++;
}
j++;
b+=_12;
}
},fromLatLngToDivPixel:function(lat,lon){
var pt=new OpenLayers.Geometry.Point(lon,lat);
pt.transform(new OpenLayers.Projection("EPSG:4326"),this.proj);
var _23=this.map.getPixelFromLonLat(new OpenLayers.LonLat(pt.x,pt.y));
return _23;
},createVLine:function(x){
var div=document.createElement("DIV");
div.style.position="absolute";
div.style.overflow="hidden";
div.style.backgroundColor=this.color;
div.style.left=x+"px";
div.style.top=this.minY+"px";
div.style.width="1px";
div.style.height=(this.maxY-this.minY)+"px";
return div;
},createHLine:function(y){
var div=document.createElement("DIV");
div.style.position="absolute";
div.style.overflow="hidden";
div.style.backgroundColor=this.color;
div.style.left=this.minX+"px";
div.style.top=y+"px";
div.style.width=(this.maxX-this.minX)+"px";
div.style.height="1px";
return div;
},createHLine3:function(lat){
var f=this.fromLatLngToDivPixel(lat,0);
var t=this.fromLatLngToDivPixel(lat,180);
var div=document.createElement("DIV");
div.style.position="absolute";
div.style.overflow="hidden";
div.style.backgroundColor=this.color;
var x1=f.x;
var x2=t.x;
if(x2<x1){
x2=f.x;
x1=t.x;
}
div.style.left=(x1-(x2-x1))+"px";
div.style.top=f.y+"px";
div.style.width=((x2-x1)*2)+"px";
div.style.height="1px";
return div;
},update:function(){
this.width=parseInt(this.objRef.targetModel.getWindowWidth());
this.height=parseInt(this.objRef.targetModel.getWindowHeight());
this.bbox=this.objRef.targetModel.getBoundingBox();
this.proj=new OpenLayers.Projection(this.objRef.targetModel.getSRS());
if(this.bbox[1]<0){
if(this.bbox[3]<0){
this.diffLat=this.bbox[1]-this.bbox[3];
}else{
this.diffLat=this.bbox[3]-this.bbox[1];
}
}else{
this.diffLat=this.bbox[3]+this.bbox[1];
}
if(this.bbox[0]<0){
if(this.bbox[2]<0){
this.diffLon=this.bbox[0]-this.bbox[2];
}else{
this.diffLon=this.bbox[2]-this.bbox[0];
}
}else{
this.diffLon=this.bbox[2]+this.bbox[0];
}
this.addGraticules();
},activate:function(){
if(OpenLayers.Control.prototype.activate.apply(this,arguments)){
this.panel_div.style.backgroundImage="url(\""+_3.enabledImage+"\")";
this.map.events.register("moveend",this,this.update);
this.objRef.display=true;
this.mapContainer=this.div;
this.color="black";
this.update();
return true;
}else{
return false;
}
},deactivate:function(){
if(OpenLayers.Control.prototype.deactivate.apply(this,arguments)){
this.panel_div.style.backgroundImage="url(\""+_3.disabledImage+"\")";
this.map.events.unregister("moveend",this,this.update);
this.objRef.display=false;
this.removeGraticules();
_3.enabled=false;
_3.doSelect(_3,false);
this.active=false;
return true;
}else{
return false;
}
}});
return _4;
};
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBase.js");
function MapScaleBar(_1,_2){
WidgetBase.apply(this,new Array(_1,_2));
this.scaleDenominator=1;
this.displaySystem=this.getProperty("mb:displaySystem","metric");
this.minWidth=this.getProperty("mb:minWidth",100);
this.maxWidth=this.getProperty("mb:maxWidth",200);
this.divisions=this.getProperty("mb:divisions",2);
this.subdivisions=this.getProperty("mb:subdivisions",2);
this.showMinorMeasures=Mapbuilder.parseBoolean(this.getProperty("mb:showMinorMeasures",false));
this.abbreviateLabel=Mapbuilder.parseBoolean(this.getProperty("mb:abbreviateLabel",false));
this.singleLine=Mapbuilder.parseBoolean(this.getProperty("mb:singleLine",false));
this.align=this.getProperty("mb:align","center");
this.resolution=72;
this.containerId=this.outputNodeId;
this.labelContainerId=this.containerId+"Label";
this.graphicsContainerId=this.containerId+"Graphics";
this.numbersContainerId=this.containerId+"Numbers";
this.model.addListener("bbox",this.update,this);
this.model.addListener("refresh",this.update,this);
}
MapScaleBar.prototype.getContainerNode=function(){
var _3=document.getElementById(this.containerId);
if(!_3){
var _3=document.createElement("div");
_3.className="sbWrapper";
_3.style.position="relative";
_3.setAttribute("id",this.containerId);
}
return _3;
};
MapScaleBar.prototype.getGraphicsContainerNode=function(){
var _4=document.getElementById(this.graphicsContainerId);
if(!_4){
var _4=document.createElement("div");
_4.style.position="absolute";
_4.className="sbGraphicsContainer";
_4.setAttribute("id",this.graphicsContainerId);
var _5=document.createElement("div");
_5.className="sbMarkerMajor";
_4.appendChild(_5);
var _6=document.createElement("div");
_6.className="sbMarkerMinor";
_4.appendChild(_6);
var _7=document.createElement("div");
_7.className="sbBar";
_4.appendChild(_7);
var _8=document.createElement("div");
_8.className="sbBarAlt";
_4.appendChild(_8);
}
return _4;
};
MapScaleBar.prototype.getLabelContainerNode=function(){
var _9=document.getElementById(this.labelContainerId);
if(!_9){
var _9=document.createElement("div");
_9.className="sbUnitsContainer";
_9.style.position="absolute";
_9.setAttribute("id",this.labelContainerId);
}
return _9;
};
MapScaleBar.prototype.getNumbersContainerNode=function(){
var _a=document.getElementById(this.numbersContainerId);
if(!_a){
var _a=document.createElement("div");
_a.style.position="absolute";
_a.className="sbNumbersContainer";
_a.setAttribute("id",this.numbersContainerId);
}
return _a;
};
MapScaleBar.prototype.update=function(_b){
var _c=document.getElementById(_b.outputNodeId);
if(!_c){
_b.getNode().appendChild(_b.getContainerNode());
}
var _d=_b.model.map.getScale();
if(_d!=null){
_b.scaleDenominator=_d;
}
function HandsomeNumber(_e,_f,_10){
var _10=(_10==null)?10:_10;
var _11=Number.POSITIVE_INFINITY;
var _12=Number.POSITIVE_INFINITY;
var _13=_e;
var _14=3;
for(var _15=0;_15<3;++_15){
var _16=Math.pow(2,(-1*_15));
var _17=Math.floor(Math.log(_f/_16)/Math.LN10);
for(var _18=_17;_18>(_17-_10+1);--_18){
var _19=Math.max(_15-_18,0);
var _1a=_16*Math.pow(10,_18);
if((_1a*Math.floor(_f/_1a))>=_e){
if(_e%_1a==0){
var _1b=_e/_1a;
}else{
var _1b=Math.floor(_e/_1a)+1;
}
var _1c=_1b+(2*_15);
var _1d=(_18<0)?(Math.abs(_18)+1):_18;
if((_1c<_11)||((_1c==_11)&&(_1d<_12))){
_11=_1c;
_12=_1d;
_13=(_1a*_1b).toFixed(_19);
_14=_19;
}
}
}
}
this.value=_13;
this.score=_11;
this.tieBreaker=_12;
this.numDec=_14;
}
HandsomeNumber.prototype.toString=function(){
return this.value.toString();
};
HandsomeNumber.prototype.valueOf=function(){
return this.value;
};
function styleValue(_1e,_1f){
var _20=0;
if(document.styleSheets){
for(var _21=document.styleSheets.length-1;_21>=0;--_21){
var _22=document.styleSheets[_21];
if(!_22.disabled){
var _23;
if(typeof (_22.rules)=="undefined"){
if(typeof (_22.rules)=="undefined"){
return 0;
}else{
_23=_22.rules;
}
}else{
_23=_22.rules;
}
for(var _24=0;_24<_23.length;++_24){
var _25=_23[_24];
if(_25.selectorText&&(_25.selectorText.toLowerCase()==_1e.toLowerCase())){
if(_25.style[_1f]!=""){
_20=parseInt(_25.style[_1f]);
}
}
}
}
}
}
return _20?_20:0;
}
function formatNumber(_26,_27){
_27=(_27)?_27:0;
var _28=""+Math.round(_26);
var _29=/(-?[0-9]+)([0-9]{3})/;
while(_29.test(_28)){
_28=_28.replace(_29,"$1,$2");
}
if(_27>0){
var _2a=Math.floor(Math.pow(10,_27)*(_26-Math.round(_26)));
if(_2a==0){
return _28;
}else{
return _28+"."+_2a;
}
}else{
return _28;
}
}
var _2b=_b.getContainerNode();
var _2c=_b.getGraphicsContainerNode();
var _2d=_b.getLabelContainerNode();
var _2e=_b.getNumbersContainerNode();
_2b.title=mbGetMessage("scale",formatNumber(_b.scaleDenominator));
var _2f=new Object();
_2f.english={units:[mbGetMessage("unitMiles"),mbGetMessage("unitFeet"),mbGetMessage("unitInches")],abbr:[mbGetMessage("unitMilesAbbr"),mbGetMessage("unitFeetAbbr"),mbGetMessage("unitInchesAbbr")],inches:[63360,12,1]};
_2f.nautical={units:[mbGetMessage("unitNauticalMiles"),mbGetMessage("unitFeet"),mbGetMessage("unitInches")],abbr:[mbGetMessage("unitNauticalMilesAbbr"),mbGetMessage("unitFeetAbbr"),mbGetMessage("unitInchesAbbr")],inches:[72913.386,12,1]};
_2f.metric={units:[mbGetMessage("unitKilometers"),mbGetMessage("unitMeters"),mbGetMessage("unitCentimeters")],abbr:[mbGetMessage("unitKilometersAbbr"),mbGetMessage("unitMetersAbbr"),mbGetMessage("unitCentimetersAbbr")],inches:[39370.07874,39.370079,0.393701]};
var _30=new Array();
for(var _31=0;_31<_2f[_b.displaySystem].units.length;++_31){
_30[_31]=new Object();
var _32=_b.resolution*_2f[_b.displaySystem].inches[_31]/_b.scaleDenominator;
var _33=(_b.minWidth/_32)/(_b.divisions*_b.subdivisions);
var _34=(_b.maxWidth/_32)/(_b.divisions*_b.subdivisions);
for(var _35=0;_35<(_b.divisions*_b.subdivisions);++_35){
var _36=_33*(_35+1);
var _37=_34*(_35+1);
var _38=new HandsomeNumber(_36,_37);
_30[_31][_35]={value:(_38.value/(_35+1)),score:0,tieBreaker:0,numDec:0,displayed:0};
for(var _39=0;_39<(_b.divisions*_b.subdivisions);++_39){
displayedValuePosition=_38.value*(_39+1)/(_35+1);
niceNumber2=new HandsomeNumber(displayedValuePosition,displayedValuePosition);
var _3a=((_39+1)%_b.subdivisions==0);
var _3b=((_39+1)==(_b.divisions*_b.subdivisions));
if((_b.singleLine&&_3b)||(!_b.singleLine&&(_3a||_b.showMinorMeasures))){
_30[_31][_35].score+=niceNumber2.score;
_30[_31][_35].tieBreaker+=niceNumber2.tieBreaker;
_30[_31][_35].numDec=Math.max(_30[_31][_35].numDec,niceNumber2.numDec);
_30[_31][_35].displayed+=1;
}else{
_30[_31][_35].score+=niceNumber2.score/_b.subdivisions;
_30[_31][_35].tieBreaker+=niceNumber2.tieBreaker/_b.subdivisions;
}
}
var _3c=(_31+1)*_30[_31][_35].tieBreaker/_30[_31][_35].displayed;
_30[_31][_35].score*=_3c;
}
}
var _3d=null;
var _3e=null;
var _3f=null;
var _40=null;
var _41=Number.POSITIVE_INFINITY;
var _42=Number.POSITIVE_INFINITY;
var _43=0;
for(var _31=0;_31<_30.length;++_31){
for(_35 in _30[_31]){
if((_30[_31][_35].score<_41)||((_30[_31][_35].score==_41)&&(_30[_31][_35].tieBreaker<_42))){
_41=_30[_31][_35].score;
_42=_30[_31][_35].tieBreaker;
_3d=_30[_31][_35].value;
_43=_30[_31][_35].numDec;
_3e=_2f[_b.displaySystem].units[_31];
_3f=_2f[_b.displaySystem].abbr[_31];
_32=_b.resolution*_2f[_b.displaySystem].inches[_31]/_b.scaleDenominator;
_40=_32*_3d;
}
}
}
var _44=(styleValue(".sbMarkerMajor","borderLeftWidth")+styleValue(".sbMarkerMajor","width")+styleValue(".sbMarkerMajor","borderRightWidth"))/2;
var _45=(styleValue(".sbMarkerMinor","borderLeftWidth")+styleValue(".sbMarkerMinor","width")+styleValue(".sbMarkerMinor","borderRightWidth"))/2;
var _46=(styleValue(".sbBar","borderLeftWidth")+styleValue(".sbBar","border-right-width"))/2;
var _47=(styleValue(".sbBarAlt","borderLeftWidth")+styleValue(".sbBarAlt","borderRightWidth"))/2;
if(!document.styleSheets){
_44=0.5;
_45=0.5;
}
while(_2d.hasChildNodes()){
_2d.removeChild(_2d.firstChild);
}
while(_2c.hasChildNodes()){
_2c.removeChild(_2c.firstChild);
}
while(_2e.hasChildNodes()){
_2e.removeChild(_2e.firstChild);
}
var _48,aBarPiece,numbersBox,xOffset;
var _49={left:0,center:(-1*_b.divisions*_b.subdivisions*_40/2),right:(-1*_b.divisions*_b.subdivisions*_40)};
var _4a=0+_49[_b.align];
var _4b=0;
for(var _4c=0;_4c<_b.divisions;++_4c){
_4a=_4c*_b.subdivisions*_40;
_4a+=_49[_b.align];
_4b=(_4c==0)?0:((_4c*_b.subdivisions)*_3d).toFixed(_43);
_48=document.createElement("div");
_48.className="sbMarkerMajor";
_48.style.position="absolute";
_48.style.overflow="hidden";
_48.style.left=Math.round(_4a-_44)+"px";
_48.appendChild(document.createTextNode(" "));
_2c.appendChild(_48);
if(!_b.singleLine){
numbersBox=document.createElement("div");
numbersBox.className="sbNumbersBox";
numbersBox.style.position="absolute";
numbersBox.style.overflow="hidden";
numbersBox.style.textAlign="center";
if(_b.showMinorMeasures){
numbersBox.style.width=Math.round(_40*2)+"px";
numbersBox.style.left=Math.round(_4a-_40)+"px";
}else{
numbersBox.style.width=Math.round(_b.subdivisions*_40*2)+"px";
numbersBox.style.left=Math.round(_4a-(_b.subdivisions*_40))+"px";
}
numbersBox.appendChild(document.createTextNode(_4b));
_2e.appendChild(numbersBox);
}
for(var _4d=0;_4d<_b.subdivisions;++_4d){
aBarPiece=document.createElement("div");
aBarPiece.style.position="absolute";
aBarPiece.style.overflow="hidden";
aBarPiece.style.width=Math.round(_40)+"px";
if((_4d%2)==0){
aBarPiece.className="sbBar";
aBarPiece.style.left=Math.round(_4a-_46)+"px";
}else{
aBarPiece.className="sbBarAlt";
aBarPiece.style.left=Math.round(_4a-_47)+"px";
}
aBarPiece.appendChild(document.createTextNode(" "));
_2c.appendChild(aBarPiece);
if(_4d<(_b.subdivisions-1)){
_4a=((_4c*_b.subdivisions)+(_4d+1))*_40;
_4a+=_49[_b.align];
_4b=(_4c*_b.subdivisions+_4d+1)*_3d;
_48=document.createElement("div");
_48.className="sbMarkerMinor";
_48.style.position="absolute";
_48.style.overflow="hidden";
_48.style.left=Math.round(_4a-_45)+"px";
_48.appendChild(document.createTextNode(" "));
_2c.appendChild(_48);
if(_b.showMinorMeasures&&!_b.singleLine){
numbersBox=document.createElement("div");
numbersBox.className="sbNumbersBox";
numbersBox.style.position="absolute";
numbersBox.style.overflow="hidden";
numbersBox.style.textAlign="center";
numbersBox.style.width=Math.round(_40*2)+"px";
numbersBox.style.left=Math.round(_4a-_40)+"px";
numbersBox.appendChild(document.createTextNode(_4b));
_2e.appendChild(numbersBox);
}
}
}
}
_4a=(_b.divisions*_b.subdivisions)*_40;
_4a+=_49[_b.align];
_4b=((_b.divisions*_b.subdivisions)*_3d).toFixed(_43);
_48=document.createElement("div");
_48.className="sbMarkerMajor";
_48.style.position="absolute";
_48.style.overflow="hidden";
_48.style.left=Math.round(_4a-_44)+"px";
_48.appendChild(document.createTextNode(" "));
_2c.appendChild(_48);
if(!_b.singleLine){
numbersBox=document.createElement("div");
numbersBox.className="sbNumbersBox";
numbersBox.style.position="absolute";
numbersBox.style.overflow="hidden";
numbersBox.style.textAlign="center";
if(_b.showMinorMeasures){
numbersBox.style.width=Math.round(_40*2)+"px";
numbersBox.style.left=Math.round(_4a-_40)+"px";
}else{
numbersBox.style.width=Math.round(_b.subdivisions*_40*2)+"px";
numbersBox.style.left=Math.round(_4a-(_b.subdivisions*_40))+"px";
}
numbersBox.appendChild(document.createTextNode(_4b));
_2e.appendChild(numbersBox);
}
var _4e=document.createElement("div");
_4e.style.position="absolute";
var _4f;
if(_b.singleLine){
_4f=_4b;
_4e.className="sbLabelBoxSingleLine";
_4e.style.top="-0.6em";
_4e.style.left=(_4a+10)+"px";
}else{
_4f="";
_4e.className="sbLabelBox";
_4e.style.textAlign="center";
_4e.style.width=Math.round(_b.divisions*_b.subdivisions*_40)+"px";
_4e.style.left=Math.round(_49[_b.align])+"px";
_4e.style.overflow="hidden";
}
if(_b.abbreviateLabel){
_4f+=" "+_3f;
}else{
_4f+=" "+_3e;
}
_4e.appendChild(document.createTextNode(_4f));
_2d.appendChild(_4e);
if(!document.styleSheets){
var _50=document.createElement("style");
_50.type="text/css";
var _51=".sbBar {top: 0px; background: #666666; height: 1px; border: 0;}";
_51+=".sbBarAlt {top: 0px; background: #666666; height: 1px; border: 0;}";
_51+=".sbMarkerMajor {height: 7px; width: 1px; background: #666666; border: 0;}";
_51+=".sbMarkerMinor {height: 5px; width: 1px; background: #666666; border: 0;}";
_51+=".sbLabelBox {top: -16px;}";
_51+=".sbNumbersBox {top: 7px;}";
_50.appendChild(document.createTextNode(_51));
document.getElementsByTagName("head").item(0).appendChild(_50);
}
_2b.appendChild(_2c);
_2b.appendChild(_2d);
_2b.appendChild(_2e);
};

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function LegendGraphic(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.model.addListener("hidden",this.refresh,this);
}
LegendGraphic.prototype.refresh=function(_3,_4){
_3.paint(_3,_3.id);
};

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function Abstract(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function FeatureInfo(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.setAttr=function(_3,_4,_5){
_3.model.setXpathValue(_3.model,_4,_5);
};
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function MapTitle(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");
function SetAoi(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.cursor="crosshair";
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{CLASS_NAME:"mbControl.SetAoi",type:OpenLayers.Control.TYPE_TOOL,draw:function(){
this.handler=new OpenLayers.Handler.Box(this,{done:this.aoiBox},{keyMask:this.keyMask});
},aoiBox:function(_5){
if(_5 instanceof OpenLayers.Bounds){
var _6=this.map.getLonLatFromPixel(new OpenLayers.Pixel(_5.left,_5.bottom));
var _7=this.map.getLonLatFromPixel(new OpenLayers.Pixel(_5.right,_5.top));
var _8=new OpenLayers.Bounds(_6.lon,_6.lat,_7.lon,_7.lat);
var _9=_8.toBBOX().split(",");
var ul=new Array(_9[0],_9[3]);
var lr=new Array(_9[2],_9[1]);
_3.targetContext.setParam("aoi",new Array(ul,lr));
_3.drawAoiBox(_3);
}
}});
this.targetContext.addListener("aoi",this.clearAoiBox,this);
return _4;
};
this.drawAoiBox=function(_c){
var _d=_c.targetContext.getParam("aoi");
var _e=new OpenLayers.Bounds(_d[0][0],_d[1][1],_d[1][0],_d[0][1]);
_c.targetContext.aoiBoxLayer=new OpenLayers.Layer.Boxes("Boxes");
_c.targetContext.map.addLayer(_c.targetContext.aoiBoxLayer);
var _f=new OpenLayers.Marker.Box(_e);
_c.targetContext.aoiBoxLayer.addMarker(_f);
};
this.clearAoiBox=function(_10){
if(_10.targetContext.aoiBoxLayer){
_10.targetContext.aoiBoxLayer.destroy();
}
};
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function ModelUrlInput(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.defaultUrl=this.getProperty("mb:defaultUrl");
this.submitForm=function(){
var _3=new Object();
_3.url=this.urlInputForm.defaultUrl.value;
_3.method=this.targetModel.method;
this.targetModel.newRequest(this.targetModel,_3);
};
this.handleKeyPress=function(_4){
var _5;
var _6;
if(_4){
_5=_4.which;
_6=_4.currentTarget;
}else{
_5=window.event.keyCode;
_6=window.event.srcElement.form;
}
if(_5==13){
_6.parentWidget.submitForm();
return false;
}
};
this.prePaint=function(_7){
_7.stylesheet.setParameter("modelTitle",_7.targetModel.title);
if(!_7.defaultUrl){
_7.defaultUrl=_7.targetModel.url;
}
_7.stylesheet.setParameter("defaultUrl",_7.defaultUrl);
};
this.postPaint=function(_8){
_8.urlInputForm=document.getElementById(_8.formName);
_8.urlInputForm.parentWidget=_8;
_8.urlInputForm.onkeypress=_8.handleKeyPress;
};
this.formName="urlInputForm_"+mbIds.getId();
this.stylesheet.setParameter("formName",this.formName);
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function CollectionList(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.switchMap=function(_3,_4){
_3.extent=_3.targetModel.map.getExtent();
_3.srs=_3.targetModel.getSRS();
_3.scale=_3.targetModel.map.getScale();
_3.targetModel.addListener("loadModel",_3.setExtent,_3);
config.loadModel(_3.targetModel.id,_4);
};
this.setExtent=function(_5){
_5.targetModel.removeListener("loadModel",_5.setExtent,_5);
var _6=_5.extent.toBBOX().split(/,/);
if(_5.targetModel.getSRS().toUpperCase()!=_5.srs.toUpperCase()){
var _7=new OpenLayers.Projection(_5.targetModel.getSRS());
var _8=new OpenLayers.Projection(_5.srs);
var _9=new OpenLayers.Geometry.Point(_6[0],_6[1]);
var _a=new OpenLayers.Geometry.Point(_6[2],_6[3]);
_9.transform(_8,_7);
_a.transform(_8,_7);
_5.extent=new OpenLayers.Bounds(_9.x,_9.y,_a.x,_a.y);
}
if(_5.targetModel.map.getExtent().containsBounds(_5.extent,false,false)){
_5.targetModel.map.zoomToExtent(_5.extent);
if(!_5.targetModel.map.fractionalZoom&&_5.targetModel.map.getScale()>_5.scale){
_5.targetModel.map.zoomIn();
}
}
};
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function SelectMapLayers(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function Timestamp(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.formName="TimestampForm_"+mbIds.getId();
this.stylesheet.setParameter("formName",this.formName);
this.updateTimestamp=function(_3,_4){
var _5=document[_3.formName];
if(_5){
_5.timestampValue.value=getNodeValue(_3.model.timestampList.childNodes[_4]);
}
};
this.model.addListener("timestamp",this.updateTimestamp,this);
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function Save(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{type:OpenLayers.Control.TYPE_BUTTON,trigger:function(){
this.map.mbMapPane.targetModel.saveModel(this.map.mbMapPane.targetModel);
},CLASS_NAME:"mbControl.Save"});
return _4;
};
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function LayerControl(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.prePaint=function(_3){
if(_3.model.featureName){
_3.stylesheet.setParameter("featureName",_3.model.featureName);
_3.stylesheet.setParameter("hidden",_3.model.getHidden(_3.model.featureName).toString());
}
};
this.highlightLayer=function(_4){
var _5=this.model.map.mbMapPane.oLlayers[_4].div;
var _6=document.getElementById("previewImage");
try{
if(_6&&_5){
_6.src=_5.firstChild.firstChild.src;
}
}
catch(e){
}
};
this.refresh=function(_7,_8){
_7.paint(_7,_7.id);
};
this.foldUnfoldGroup=function(_9,id){
var _b="//wmc:General/wmc:Extension/wmc:GroupList/wmc:Group[@name='"+_9+"']";
var _c=_2.doc.selectSingleNode(_b);
var _d=_c.getAttribute("folded");
var e=document.getElementById(id);
if(_d=="1"){
_c.setAttribute("folded","0");
e.value="-";
}else{
_c.setAttribute("folded","1");
e.value="+";
}
};
this.showLayerMetadata=function(_f){
var _10=config.objects.layerMetadata;
if(_10){
_10.stylesheet.setParameter("featureName",_f);
_10.node=document.getElementById(_10.htmlTagId);
_10.paint(_10);
}
};
this.ChangeImage=function(id,_12,_13){
var _14=document.getElementById(id).src.indexOf(_12);
var _15=document.getElementById(id).src.indexOf(_13);
if(document.getElementById(id)!=null){
if(_14!=-1){
document.getElementById(id).src=document.getElementById(id).src.substring(0,_14)+_13;
}else{
document.getElementById(id).src=document.getElementById(id).src.substring(0,_15)+_12;
}
}
return;
};
this.switchVisibilityById=function(id){
e=document.getElementById(id);
if(e.style.display=="none"){
e.style.display="block";
}else{
e.style.display="none";
}
};
this.model.addListener("deleteLayer",this.refresh,this);
this.model.addListener("moveLayerUp",this.refresh,this);
this.model.addListener("moveLayerDown",this.refresh,this);
if(this.autoRefresh){
this.model.addListener("addLayer",this.refresh,this);
}
}

js: Couldn't open file "/home/cameron/tmp/mapbuilder/build/../compressBuild/lib/widget/AoiBoxDHTML.js".
mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function FilterAttributes(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function SelectFeatureType(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function AoiForm(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.displayAoiCoords=function(_3,_4){
var _5=document.getElementById(_3.formName);
var _6=_3.model.getParam("aoi");
if(_6&&_5){
_5.westCoord.value=_6[0][0];
_5.northCoord.value=_6[0][1];
_5.eastCoord.value=_6[1][0];
_5.southCoord.value=_6[1][1];
}
};
this.model.addListener("aoi",this.displayAoiCoords,this);
this.setAoi=function(){
var _7=this.model.getParam("aoi");
if(_7){
var ul=_7[0];
var lr=_7[1];
switch(this.name){
case "westCoord":
ul[0]=this.value;
break;
case "northCoord":
ul[1]=this.value;
break;
case "eastCoord":
lr[0]=this.value;
break;
case "southCoord":
lr[1]=this.value;
break;
}
this.model.setParam("aoi",new Array(ul,lr));
}
};
this.postPaint=function(_a){
var _b=document.getElementById(_a.formName);
_b.westCoord.onblur=_a.setAoi;
_b.northCoord.onblur=_a.setAoi;
_b.eastCoord.onblur=_a.setAoi;
_b.southCoord.onblur=_a.setAoi;
_b.westCoord.model=_a.model;
_b.northCoord.model=_a.model;
_b.eastCoord.model=_a.model;
_b.southCoord.model=_a.model;
};
this.formName=this.getProperty("mb:formName","AoiForm_"+mbIds.getId());
this.stylesheet.setParameter("formName",this.formName);
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function Widget(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function WebServiceForm(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.formElements=new Object();
var _3=this.getProperty("mb:requestStylesheet");
if(_3){
this.requestStylesheet=new XslProcessor(_3,_2.namespace);
}
this.webServiceUrl=this.getProperty("mb:webServiceUrl");
this.submitForm=function(){
this.webServiceForm=document.getElementById(this.formName);
if(this.webServiceForm==null){
this.webServiceForm=document.getElementById("webServiceForm_form");
}
if(this.webServiceForm==null){
return;
}
var _4=new Object();
_4.method=this.targetModel.method;
_4.url=this.webServiceUrl;
if(_4.method.toLowerCase()=="get"){
_4.url=this.webServiceForm.action+"?";
for(var i=0;i<this.webServiceForm.elements.length;++i){
var _6=this.webServiceForm.elements[i];
webServiceUrl+=_6.name+"="+_6.value+"&";
this.formElements[_6.name]=_6.value;
}
mbDebugMessage(this,_4.url);
this.targetModel.newRequest(this.targetModel,_4);
}else{
for(var i=0;i<this.webServiceForm.elements.length;++i){
var _6=this.webServiceForm.elements[i];
this.formElements[_6.name]=_6.value;
}
this.requestStylesheet.setParameter("resourceName",this.formElements["feature"]);
this.requestStylesheet.setParameter("fromDateField",this.formElements["fromDateField"]);
this.requestStylesheet.setParameter("toDateField",this.formElements["toDateField"]);
var _7=this.requestStylesheet.transformNodeToObject(this.model.doc);
if(this.debug){
mbDebugMessage(this,"Transformed: "+(new XMLSerializer()).serializeToString(_7));
}
this.namespace="xmlns:wmc='http://www.opengis.net/context' xmlns:ows='http://www.opengis.net/ows' xmlns:ogc='http://www.opengis.net/ogc' xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:gml='http://www.opengis.net/gml' xmlns:wfs='http://www.opengis.net/wfs'";
Sarissa.setXpathNamespaces(_7,this.namespace);
var _8=_7.selectSingleNode("//wfs:GetFeature");
_4.postData=(new XMLSerializer()).serializeToString(_8);
mbDebugMessage(this,"httpPayload.postData:"+_4.postData);
this.targetModel.wfsFeature=_7.childNodes[0];
if(this.debug){
mbDebugMessage(this,"wfsFeature = "+(new XMLSerializer()).serializeToString(this.targetModel.wfsFeature));
}
this.targetModel.newRequest(this.targetModel,_4);
}
};
this.handleKeyPress=function(_9){
var _a;
var _b;
if(_9){
_a=_9.which;
_b=_9.currentTarget;
}else{
_a=window.event.keyCode;
_b=window.event.srcElement.form;
}
if(_a==13){
_b.parentWidget.submitForm();
return false;
}
};
this.postPaint=function(_c){
_c.webServiceForm=document.getElementById(_c.formName);
if(this.webServiceForm==null){
this.webServiceForm=document.getElementById("webServiceForm_form");
}
_c.webServiceForm.parentWidget=_c;
_c.webServiceForm.onkeypress=_c.handleKeyPress;
};
this.formName="WebServiceForm_"+mbIds.getId();
this.stylesheet.setParameter("formName",this.formName);
this.prePaint=function(_d){
for(var _e in _d.formElements){
_d.stylesheet.setParameter(_e,_d.formElements[_e]);
}
};
this.setAoiParameters=function(_f,_10){
if(_f.model){
var _11=null;
var _12=_f.model.getSRS();
_f.requestStylesheet.setParameter("bBoxMinX",_10[0][0]);
_f.requestStylesheet.setParameter("bBoxMinY",_10[1][1]);
_f.requestStylesheet.setParameter("bBoxMaxX",_10[1][0]);
_f.requestStylesheet.setParameter("bBoxMaxY",_10[0][1]);
_f.requestStylesheet.setParameter("srs",_12);
_f.requestStylesheet.setParameter("width",_f.model.getWindowWidth());
_f.requestStylesheet.setParameter("height",_f.model.getWindowHeight());
}
};
this.init=function(_13){
if(_13.model){
_13.model.addListener("aoi",_13.setAoiParameters,_13);
}
};
this.model.addListener("init",this.init,this);
}

mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
function GetFeatureInfo(_1,_2){
ButtonBase.apply(this,new Array(_1,_2));
this.xsl=new XslProcessor(this.getProperty("mb:stylesheet",baseDir+"/tool/GetFeatureInfo.xsl"));
this.infoFormat=this.getProperty("mb:infoFormat","application/vnd.ogc.gml");
this.featureCount=this.getProperty("mb:featureCount",1);
this.cursor="pointer";
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control,{CLASS_NAME:"mbControl.GetFeatureInfo",type:OpenLayers.Control.TYPE_TOOL,draw:function(){
this.handler=new OpenLayers.Handler.Box(this,{done:this.zoomBox},{keyMask:this.keyMask});
},zoomBox:function(_5){
var _6=this.objRef;
if(_6.enabled){
var x,y;
if(_5 instanceof OpenLayers.Bounds){
x=_5.left+(_5.right-_5.left)/2;
y=_5.top+(_5.bottom-_5.top)/2;
}else{
x=_5.x;
y=_5.y;
}
_6.targetModel.deleteTemplates();
var _8=_6.targetContext.getParam("selectedLayer");
if(_8==null){
var _9=_6.targetContext.getQueryableLayers();
if(_9.length==0){
alert(mbGetMessage("noQueryableLayers"));
return;
}else{
for(var i=0;i<_9.length;++i){
var _b=_9[i];
var _c=Mapbuilder.getProperty(_b,"wmc:Name","");
var _d=_b.getAttribute("id")||_c;
var _e=_6.targetContext.getHidden(_d);
if(_e==0){
_6.xsl.setParameter("queryLayer",_c);
_6.xsl.setParameter("layer",_c);
_6.xsl.setParameter("xCoord",x);
_6.xsl.setParameter("yCoord",y);
_6.xsl.setParameter("infoFormat",_6.infoFormat);
_6.xsl.setParameter("featureCount",_6.featureCount);
urlNode=_6.xsl.transformNodeToObject(_6.targetContext.doc);
url=getNodeValue(urlNode.documentElement);
httpPayload=new Object();
httpPayload.url=url;
httpPayload.method="get";
httpPayload.postData=null;
_6.targetModel.newRequest(_6.targetModel,httpPayload);
}
}
}
}else{
_6.xsl.setParameter("queryLayer",_8);
_6.xsl.setParameter("layer",_c);
_6.xsl.setParameter("xCoord",x);
_6.xsl.setParameter("yCoord",y);
_6.xsl.setParameter("infoFormat",_6.infoFormat);
_6.xsl.setParameter("featureCount",_6.featureCount);
var _f=_6.xsl.transformNodeToObject(_6.targetContext.doc);
var url=getNodeValue(_f.documentElement);
if(_6.infoFormat=="text/html"){
window.open(url,"queryWin","height=200,width=300,scrollbars=yes");
}else{
var _11=new Object();
_11.url=url;
_11.method="get";
_11.postData=null;
_6.targetModel.newRequest(_6.targetModel,_11);
}
}
}
}});
return _4;
};
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function Locations(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.setAoi=function(_3,_4,_5){
var _6=_5.split(/[:#]/);
_5="EPSG:"+_6[_6.length-1];
if(!_5){
_5="EPSG:4326";
}
var _7=new OpenLayers.Projection(_5);
var _8=new Array();
_8=_3.split(",");
var _9=new OpenLayers.Geometry.Point(parseFloat(_8[0]),parseFloat(_8[3]));
var _a=new OpenLayers.Geometry.Point(parseFloat(_8[2]),parseFloat(_8[1]));
_9.transform(_7,this.targetModel.proj);
_a.transform(_7,this.targetModel.proj);
var ul=new Array(_9.x,_9.y);
var lr=new Array(_a.x,_a.y);
this.targetModel.setParam("aoi",new Array(ul,lr));
this.targetModel.map.zoomToExtent(new OpenLayers.Bounds(ul[0],lr[1],lr[0],ul[1]));
};
}

mapbuilder.loadScript(baseDir+"/widget/EditButtonBase.js");
function Measurement(_1,_2){
EditButtonBase.apply(this,new Array(_1,_2));
this.createControl=function(_3){
var _4=OpenLayers.Class(OpenLayers.Control.DrawFeature,{CLASS_NAME:"mbMeasurement"});
return _4;
};
this.instantiateControl=function(_5,_6){
var _7=new _6(_5.featureLayer,OpenLayers.Handler.Path,{callbacks:{point:_5.startAction}});
_7.objRef=_5;
_7.activate=function(){
_6.prototype.activate.apply(this,arguments);
_5.targetModel.setParam("showDistance",0);
};
_7.deactivate=function(){
_6.prototype.deactivate.apply(this,arguments);
_5.targetModel.setParam("showDistance",null);
};
return _7;
};
this.cursor="crosshair";
var _8=0;
var _9=0;
var _a=false;
var _b=false;
this.startAction=function(_c){
var _d=this.objRef;
_d.pointGeometry=_c;
if(!_d.targetModel.doc){
_d.targetModel.addListener("loadModel",_d.doAction,_d);
config.loadModel(_d.targetModel.id,_d.defaultModelUrl);
}else{
_d.doAction(_d);
}
};
this.doAction=function(_e){
pointGeometry=_e.pointGeometry;
_e.targetModel.removeListener("loadModel",_e.doAction,_e);
if(_e.enabled){
if(_e.restart){
_e.model.setParam("clearMeasurementLine");
_e.restart=false;
}
var _f=[pointGeometry.x,pointGeometry.y];
var old=_e.targetModel.getXpathValue(_e.targetModel,_e.featureXpath);
if(!old){
old="";
}
sucess=_e.targetModel.setXpathValue(_e.targetModel,_e.featureXpath,old+" "+_f[0]+","+_f[1]);
if(!sucess){
alert(mbGetMessage("invalidFeatureXpathMeasurement",_e.featureXpath));
}
var _11=_e.targetModel.getXpathValue(_e.targetModel,_e.featureXpath);
var _12=_11.split(" ");
if(_12.length>=3){
var _13=_12[_12.length-2];
var _14=_12[_12.length-1];
var P=_13.split(",");
var Q=_14.split(",");
_e.srs=srs.toUpperCase();
_e.proj=new OpenLayers.Projection(_e.srs);
if(!P||!Q){
alert(mbGetMessage("projectionNotSupported"));
}else{
if(_e.proj.getUnits()=="meters"||_e.proj.getUnits()=="m"){
Xp=parseFloat(P[0]);
Yp=parseFloat(P[1]);
Xq=parseFloat(Q[0]);
Yq=parseFloat(Q[1]);
_9=Math.sqrt(((Xp-Xq)*(Xp-Xq))+((Yp-Yq)*(Yp-Yq)));
if(_9==0){
_e.restart=true;
_e.model.setParam("clearMouseLine");
_e.targetModel.setParam("mouseRenderer",false);
return;
}
_8=Math.round(_8+_9);
}else{
if(_e.proj.getUnits()=="degrees"||_e.proj.getUnits()==null){
var _17=Math.PI/180;
var _18=new Array(0,0);
LonpRad=parseFloat(P[0])*_17;
LatpRad=parseFloat(P[1])*_17;
LonqRad=parseFloat(Q[0])*_17;
LatqRad=parseFloat(Q[1])*_17;
if((LonpRad>0&&LonqRad<0)||(LonpRad<0&&LonqRad>0)){
if(LonpRad<0){
radDistance=Math.acos(Math.sin(LatpRad)*Math.sin(LatqRad)+Math.cos(LatpRad)*Math.cos(LatqRad)*Math.cos(LonpRad));
radDistance+=Math.acos(Math.sin(LatpRad)*Math.sin(LatqRad)+Math.cos(LatpRad)*Math.cos(LatqRad)*Math.cos(LonqRad));
}
if(LonqRad<0){
radDistance=Math.acos(Math.sin(LatpRad)*Math.sin(LatqRad)+Math.cos(LatpRad)*Math.cos(LatqRad)*Math.cos(LonqRad));
radDistance+=Math.acos(Math.sin(LatpRad)*Math.sin(LatqRad)+Math.cos(LatpRad)*Math.cos(LatqRad)*Math.cos(LonpRad));
}
}else{
radDistance=Math.acos(Math.sin(LatpRad)*Math.sin(LatqRad)+Math.cos(LatpRad)*Math.cos(LatqRad)*Math.cos(LonpRad-LonqRad));
}
_9=radDistance*6378137;
if(_9==0){
_e.restart=true;
_e.model.setParam("clearMouseLine");
_e.targetModel.setParam("mouseRenderer",false);
return;
}
_8=Math.round(_8+_9);
}else{
alert(mbGetMessage("cantCalculateDistance"));
}
}
}
}
_e.targetModel.setParam("showDistance",_8);
}
};
this.setFeature=function(_19,_1a){
_19.restart=true;
};
this.clearMeasurementLine=function(_1b){
if(_8!=0){
_8=0;
sucess=_1b.targetModel.setXpathValue(_1b.targetModel,_1b.featureXpath,"");
if(!sucess){
alert(mbGetMessage("invalidFeatureXpathMeasurement",_1b.featureXpath));
}
_1b.targetModel.setParam("refresh");
}
};
this.model.addListener("clearMeasurementLine",this.clearMeasurementLine,this);
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function ModelStatus(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.prePaint=function(_3){
_3.stylesheet.setParameter("statusMessage",_3.targetModel.getParam("modelStatus"));
};
this.model.addListener("modelStatus",this.paint,this);
}

js: Couldn't open file "/home/cameron/tmp/mapbuilder/build/../compressBuild/lib/widget/SaveModel.js".
mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function ShowDistance(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
this.showDistance=function(_3){
var _4=document.getElementById(_3.formName);
var _5=_3.model.values.showDistance;
if(_5==null){
_3.getNode().style.display="none";
}else{
_3.getNode().style.display="";
if(_5>1000){
if(_5>1000000){
outputDistance=Math.round(_5/1000)+"  km";
}else{
outputDistance=Math.round(_5/100)/10+"  km";
}
}else{
if(_5>0){
outputDistance=Math.round(_5)+"  m";
}else{
outputDistance="";
}
}
if(_4){
_4.distance.value=outputDistance;
}
}
};
this.model.addListener("showDistance",this.showDistance,this);
this.formName="ShowDistance_"+mbIds.getId();
this.stylesheet.setParameter("formName",this.formName);
}

mapbuilder.loadScript(baseDir+"/widget/WidgetBaseXSL.js");
function SelectAllMapLayers(_1,_2){
WidgetBaseXSL.apply(this,new Array(_1,_2));
}

