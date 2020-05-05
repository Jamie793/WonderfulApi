function test(t)return t end
function eval(t)
local f,s=loadstring(t)
  if f then
    local s,r=pcall(f)
    return r
  end
  return s
end
function main(t)return [[
<html>
<body>
luaj http server test
</body>
</html>

]] end