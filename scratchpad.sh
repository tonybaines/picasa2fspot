#!/bin/sh

get_subjects() {
    exiftool -Subject "$1" | awk -F: '{print $2}' | sort | uniq
}
set_subjects() {
    exiftool -Subject="$1" "$2"
}
IMAGE="Winter/SG104404.JPG"

set_subjects "winter" $IMAGE

subjects=`get_subjects $IMAGE`

echo "Subjects:$subjects"

set_subjects "$subjects, Favourites" $IMAGE
get_subjects $IMAGE
set_subjects "$subjects" $IMAGE
get_subjects $IMAGE
