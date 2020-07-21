rm All.txt
rm wiki.html
cp All.txt.original All.txt
cp wiki.html.original wiki.html
#java -Djava.util.logging.config.file=./logging.properties -cp target/*with*.jar fdshow.App All.txt wiki.html
#java -cp target/*with*.jar fdshow.App -l logging.properties All.txt wiki.html
java -cp target/*with*.jar fdshow.App All.txt wiki.html
