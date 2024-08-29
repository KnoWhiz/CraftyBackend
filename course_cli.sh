#!/bin/bash

BASE_URL="http://localhost:8081"

function create_course() {
    echo "Creating course..."
    echo "TOPIC: $TOPIC"
    echo "TYPE: $TYPE"

    # Send POST request to create course
    RESPONSE=$(curl -s -X POST "$BASE_URL/course" \
        -H "Content-Type: application/json" \
        -d "{\"topic\": \"$TOPIC\", \"type\": \"$TYPE\"}")

    # Extract course_id from response
    COURSE_ID=$(echo "$RESPONSE" | jq -r '.course_id')

    # Check if course_id was extracted successfully
    if [ "$COURSE_ID" != "null" ] && [ -n "$COURSE_ID" ]; then
        echo "Course created successfully with ID: $COURSE_ID"
        echo "Use the following command to generate chapter:"
        echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step CHAPTER --parameters \"{\"topic\": ""$TOPIC""}\""
    else
        echo "Failed to create course. Response: $RESPONSE"
    fi
    echo
}


function create_step() {
    echo "Creating step..."

    # Define variables for step type and parameters
#    STEP_NAME=$1
#    COURSE_ID=$2
#    PARAMS=$3
  echo "COURSE_ID: $COURSE_ID"
    echo "STEP: $STEP_NAME"
    echo "PARAMETERS: $PARAMS"


    RESPONSE=$(curl -s -X POST "$BASE_URL/step" \
    -H "Content-Type: application/json" \
    -d "{ \"courseId\": \"$COURSE_ID\", \"stepType\": \"$STEP_NAME\", \"parameters\": $PARAMS }")



    # Based on the step name, echo different messages
    case $STEP_NAME in
        "chapter")
            echo "Chapter step created with parameters: $PARAMS"
            echo "Next step might be SECTION"
            ;;
        "section")
            echo "Section step created with parameters: $PARAMS"
            echo "Next step might be NOTE"
            ;;
        "note")
            echo "Note step created with parameters: $PARAMS"
            echo "Next step might be SLIDE"
            ;;
        "slide")
            echo "Slide step created with parameters: $PARAMS"
            echo "Next step might be SCRIPT"
            ;;
        "script")
            echo "Script step created with parameters: $PARAMS"
            echo "Next step might be VOICE_OVER"
            ;;
        "voice_over")
            echo "Voice-over step created with parameters: $PARAMS"
            echo "Next step might be VIDEO"
            ;;
        "video")
            echo "Video step created with parameters: $PARAMS"
            echo "All steps completed!"
            ;;
        *)
            echo "Unknown step name: $STEP_NAME"
            ;;
    esac

    echo "Step creation response: $RESPONSE"
    echo
}

function get_course() {
    echo "Fetching course..."
    curl -X GET "$BASE_URL/course/your_course_id"
    echo
}

function get_step_status() {
    echo "Fetching step status..."
    curl -X GET "$BASE_URL/step/your_step_id"
    echo
}

if [ "$1" == "create-course" ]; then
    TOPIC=$3
    TYPE=$5
    create_course
elif [ "$1" == "create-step" ]; then
    STEP_NAME=$5
    COURSE_ID=$3
    PARAMS=$7
    create_step
else
    echo "Invalid command. Usage: ./course_cli.sh [create-course | create-step]"
fi


#
#BASE_URL="http://localhost:8081"
#
## Function to create a course
#create_course() {
#    local topic=$1
#    local type=$2
#     response=$(curl -s -X POST "$BASE_URL/course" \
#            -H "Content-Type: application/json" \
#                    -d '{
#                          "topic": "'"$topic"'",
#                          "type": "'"$type"'"
#                        }')
#
#        course_id=$(echo "$response" | jq -r '.course_id')
#
#            echo "Course created with ID: $course_id"
#
#        # Check if the course_id was extracted successfully
#        if [ "$course_id" != "null" ] && [ -n "$course_id" ]; then
#            echo "Course created successfully with ID: $course_id"
#            echo "Use the following command to generate chapter:"
#            echo "./course_cli.sh create-step -course-id \"$course_id -parameters \"{topic":"$topic""}\""
#        else
#            echo "Failed to create course. Response: $response"
#        fi
#        echo
#
#}
#
## Function to create a step
#create_step() {
#    local course_id=$1
#    local step_type=$2
#    local parameters=$3
#
#    curl -X POST "http://localhost:8080/step" \
#    -H "Content-Type: application/json" \
#    -d '{
#          "course_id": "'"$course_id"'",
#          "step": "'"$step_type"'",
#          "parameters": "'"$parameters"'"
#        }'
#
#    echo "Step '$step_type' created for course_id '$course_id'."
#}
#
## Main function to handle user input
#main() {
#    if [ "$2" == "create_course" ]; then
#        create_course "$2" "$3"
#    elif [ "$2" == "create_step" ]; then
#        create_step "$2" "$3" "$4"
#    else
#        echo "Invalid command. Usage: ./your_script.sh [create_course|create_step]"
#    fi
#}
#
#main "$@"

