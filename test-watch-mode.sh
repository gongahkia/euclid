#!/bin/bash

# Test script for watch mode functionality
# This demonstrates that watch mode correctly monitors file changes

echo "=== Testing Euclid Watch Mode ==="
echo

# Create a test file
TEST_FILE="/tmp/euclid_watch_test.ed"
OUTPUT_FILE="/tmp/euclid_watch_test.md"

echo "sin(x)" > "$TEST_FILE"
echo "✓ Created test file: $TEST_FILE"
echo

# Start watch mode in background
echo "Starting watch mode in background..."
cd /home/gongahkia/Desktop/coding/projects/euclid
mvn -q exec:java -Dexec.mainClass="com.euclid.Transpiler" -Dexec.args="--watch $TEST_FILE $OUTPUT_FILE" > /tmp/watch_output.log 2>&1 &
WATCH_PID=$!
echo "✓ Watch mode started (PID: $WATCH_PID)"
echo

# Wait for initial transpilation
sleep 3

# Check initial output
echo "Checking initial transpilation..."
if [ -f "$OUTPUT_FILE" ]; then
    echo "✓ Output file created"
    echo "Content: $(cat $OUTPUT_FILE)"
else
    echo "✗ Output file not created"
    kill $WATCH_PID 2>/dev/null
    exit 1
fi
echo

# Modify the file
echo "Modifying input file..."
echo "cos(x)" > "$TEST_FILE"
sleep 2

# Check if retranspilation occurred
echo "Checking retranspilation..."
NEW_CONTENT=$(cat "$OUTPUT_FILE")
if [[ "$NEW_CONTENT" == *"cos"* ]]; then
    echo "✓ File was retranspiled successfully"
    echo "New content: $NEW_CONTENT"
else
    echo "✗ File was not retranspiled"
    kill $WATCH_PID 2>/dev/null
    exit 1
fi
echo

# Show watch log
echo "Watch mode log:"
cat /tmp/watch_output.log
echo

# Cleanup
echo "Cleaning up..."
kill $WATCH_PID 2>/dev/null
rm -f "$TEST_FILE" "$OUTPUT_FILE" /tmp/watch_output.log
echo "✓ Test complete!"
