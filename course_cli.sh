#!/bin/bash

BASE_URL="http://localhost:8081"

function create_course() {
    echo "Creating course..."

    RESPONSE=$(curl -s -X POST "$BASE_URL/course" \
        -H "Content-Type: application/json" \
        -d "{\"topic\": \"$TOPIC\", \"type\": \"$TYPE\"}")
    # get CourseId
    COURSE_ID=$(echo "$RESPONSE" | jq -r '.course_id')

    if [ "$COURSE_ID" != "null" ] && [ -n "$COURSE_ID" ]; then
      PARAMS=$(jq -n --arg topic "$TOPIC" '{topic: $topic}')
        echo "Course created successfully with ID: $COURSE_ID"
        echo "----------------------------------------------"
        echo "Use the following command to generate chapter:"
        #echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step chapter --parameters '$PARAMS'"

        echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step chapter --parameters '{\"topic\": \""$TOPIC"\"}'"
    else
        echo "Failed to create course. Response: $RESPONSE"
    fi
    echo
}


function create_step() {
    echo "Creating step..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/step" \
    -H "Content-Type: application/json" \
    -d "{ \"courseId\": \"$COURSE_ID\", \"stepType\": \"$STEP_NAME\", \"parameters\": $PARAMS }")

    # Based on the step name, echo different messages
    case $STEP_NAME in
        "chapter")
            echo "Chapter step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "Use the following command to generate chapter:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step section --parameters '{\"sections_per_chapter\": 20}'"

            ;;
        "section")
            echo "Section step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "Use the following command to generate note:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step note --parameters '{\"max_note_expansion_words\": 500, \"chapter\": 0}'"

            ;;
        "note")
            echo "Note step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "Use the following command to generate slide:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step slide --parameters '{\"slides_template_file\": 3, \"content_slide_pages\": 30}'"

            ;;
        "slide")
            echo "Slide step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "Use the following command to generate script:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step script --parameters '{\"chapter\": 0}'"

            ;;
        "script")
            echo "Script step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "Use the following command to generate voice_over:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step voice_over --parameters '{\"chapter\": 0}'"

            ;;
        "voice_over")
            echo "Voice-over step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "Use the following command to generate video:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step video --parameters '{\"chapter\": 0}'"

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