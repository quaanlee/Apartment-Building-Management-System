with open("src/main/resources/static/js/manager/revenue.js", "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace("var gapPct = 1.5;", "var gapPct = 3;")
content = content.replace('\n            circle.setAttribute("stroke-linecap", "round");', "")

with open("src/main/resources/static/js/manager/revenue.js", "w", encoding="utf-8") as f:
    f.write(content)

print("JS fixed")
