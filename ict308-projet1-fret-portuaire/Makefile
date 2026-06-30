SRC_DIR := src/main/java
OUT_DIR := out
MAIN_CLASS := cm.uy1.ict308.fret.Main

.PHONY: compile run clean

compile:
	mkdir -p $(OUT_DIR)
	javac -encoding UTF-8 -d $(OUT_DIR) $$(find $(SRC_DIR) -name "*.java")

run: compile
	java -cp $(OUT_DIR) $(MAIN_CLASS)

clean:
	rm -rf $(OUT_DIR) data/fret.ser
