with open("src/main/resources/static/css/manager/revenue.css", "r", encoding="utf-8") as f:
    css = f.read()
css = css.replace("overflow-y: auto;\n    overflow: hidden;", "overflow-y: auto;")
with open("src/main/resources/static/css/manager/revenue.css", "w", encoding="utf-8") as f:
    f.write(css)
print("Fixed overflow")
