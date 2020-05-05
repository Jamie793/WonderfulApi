print( 'hello, world' )
local t=os.clock()
local al = luajava.newInstance("java.util.ArrayList");

for n=1,10000000 do
    al:add(n)
end
print(os.clock()-t)