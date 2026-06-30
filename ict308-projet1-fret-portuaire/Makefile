SRC_DIR := src/main/java
TEST_DIR := src/test/java
OUT_DIR := out
MAIN_CLASS := cm.uy1.ict308.fret.Main
TEST_CLASS := cm.uy1.ict308.fret.FretPortuaireTest

.PHONY: compile test run clean

compile:
	mkdir -p $(OUT_DIR)
	javac -encoding UTF-8 -d $(OUT_DIR) $$(find $(SRC_DIR) -name "*.java")

test: compile
	javac -encoding UTF-8 -cp $(OUT_DIR) -d $(OUT_DIR) $$(find $(TEST_DIR) -name "*.java")
	java -ea -cp $(OUT_DIR) $(TEST_CLASS)

run: compile
	java -cp $(OUT_DIR) $(MAIN_CLASS)

clean:
	rm -rf $(OUT_DIR) data/fret.ser
