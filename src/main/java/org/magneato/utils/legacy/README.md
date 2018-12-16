Install

npm install elasticdump

if you have problems with certs turn strict ssl off

npm config set strict-ssl false

dump data from index

~/node_modules/elasticdump/bin/elasticdump --input=http://localhost:9200/my-index   --output=./my_index_mapping.json --type=data

output is pseudo json, one json record per line:

{"_index":"my-index","_type":"_doc","_id":"ZRqdPWcBFZwRcx77qpZv","_score":1,"_source":{"title":"Snowga sucks","feedback":"<p>I think that snowga really sucks donkey balls !</p><p><iframe src=\"//www.youtube.com/embed/PVJunr77pGE\" class=\"note-video-clip\" width=\"640\" height=\"360\" frameborder=\"0\"></iframe><br></p>","files":[],"category":"Technology","metadata":{"edit_template":"simple","display_template":"simple","create_date":"2018-11-22 23:48:52","ip_addr":"127.0.0.1","owner":"admin","canonical_url":"snowga-sucks","relations":["ZBqXPWcBFZwRcx772JZr"],"groups":["default"]}}}

to import to the index

 ~/node_modules/elasticdump/bin/elasticdump --bulk --output="http://localhost:9200/"  --input=./index.json
