cd ../src/
find . -name '*.java' | xargs wc -l
echo 'Lines in:'
find . -type f | wc -l
echo 'Files'