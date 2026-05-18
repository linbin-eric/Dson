package cc.jfire.dson.reader;

import cc.jfire.baseutil.reflect.type.ParameterizedTypeResolver;
import cc.jfire.dson.DsonConfig;
import cc.jfire.dson.DsonContext;
import cc.jfire.dson.reader.impl.CollectionReader;
import cc.jfire.dson.reader.impl.MapReader;
import cc.jfire.dson.reader.impl.NewArrayReader;
import cc.jfire.dson.reader.impl.ObjectReader;
import cc.jfire.dson.reader.impl.UnKnowTypeReader;
import cc.jfire.dson.reader.impl.basic.EnumNameReader;
import lombok.SneakyThrows;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReaderContext
{
    private final Type                                    root;
    private final DsonContext                             dsonContext;
    private final DsonConfig                              config;
    private final ParameterizedTypeResolver               parameterizedTypeResolver;
    private final ConcurrentHashMap<Type, ParameterizedTypeResolver> parameterizedTypeResolvers = new ConcurrentHashMap<Type, ParameterizedTypeResolver>();
    private final Map<Type, TypeReader>                   standardReaders;
    private final ConcurrentHashMap<Type, TypeReader>     readers         = new ConcurrentHashMap<Type, TypeReader>();
    private final ThreadLocal<Map<Type, TypeReader>>      buildingReaders = ThreadLocal.withInitial(HashMap::new);
    private final TypeReader                              rootReader;

    public ReaderContext(Type root, DsonContext dsonContext, Map<Type, TypeReader> standardReaders)
    {
        this.root                      = root;
        this.dsonContext               = dsonContext;
        this.config                    = dsonContext.getConfig();
        this.standardReaders           = standardReaders;
        this.parameterizedTypeResolver = new ParameterizedTypeResolver(resolverSource(root));
        this.rootReader                = parseReader(root);
    }

    public Type getRoot()
    {
        return root;
    }

    public DsonContext getDsonContext()
    {
        return dsonContext;
    }

    public DsonConfig getConfig()
    {
        return config;
    }

    public TypeReader getRootReader()
    {
        return rootReader;
    }

    public Type resolveType(Type type)
    {
        return parameterizedTypeResolver.resolveType(type);
    }

    public Type resolveType(Type ownerType, Type type)
    {
        Type                      resolvedOwnerType = resolveType(ownerType);
        ParameterizedTypeResolver resolver          = parameterizedTypeResolvers.computeIfAbsent(resolvedOwnerType, each -> new ParameterizedTypeResolver(resolverSource(each)));
        return resolveType(resolver.resolveType(type));
    }

    public TypeReader parseReader(Type type)
    {
        Type       lookupType = resolveType(type);
        TypeReader typeReader = standardReaders.get(lookupType);
        if (typeReader != null)
        {
            return typeReader;
        }
        typeReader = readers.get(lookupType);
        if (typeReader != null)
        {
            return typeReader;
        }
        Map<Type, TypeReader> building = buildingReaders.get();
        typeReader = building.get(lookupType);
        if (typeReader != null)
        {
            return typeReader;
        }
        typeReader = buildTypeReader(lookupType);
        building.put(lookupType, typeReader);
        try
        {
            typeReader.initialize(lookupType, this);
            readers.put(lookupType, typeReader);
            return typeReader;
        }
        finally
        {
            building.remove(lookupType);
        }
    }

    public TypeReader parseReader(Type ownerType, Type type)
    {
        return parseReader(resolveType(ownerType, type));
    }

    @SneakyThrows
    private TypeReader buildTypeReader(Type type)
    {
        if (type instanceof GenericArrayType)
        {
            return new NewArrayReader();
        }
        Class<?> rawType = rawTypeOf(type);
        if (rawType.isAnnotationPresent(DeSerializeDefinition.class))
        {
            try
            {
                return rawType.getAnnotation(DeSerializeDefinition.class).value().getConstructor().newInstance();
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (rawType.isArray())
        {
            return new NewArrayReader();
        }
        if (Collection.class.isAssignableFrom(rawType))
        {
            return new CollectionReader();
        }
        if (Map.class.isAssignableFrom(rawType))
        {
            return new MapReader();
        }
        if (Enum.class.isAssignableFrom(rawType))
        {
            return new EnumNameReader();
        }
        if (rawType == Object.class)
        {
            return new UnKnowTypeReader();
        }
        if (rawType.isRecord())
        {
            return new ObjectReader();
        }
        return config.isReadUseCompile() ? TypeReader.compile(rawType) : new ObjectReader();
    }

    private static Class<?> rawTypeOf(Type type)
    {
        if (type instanceof Class<?> clazz)
        {
            return clazz;
        }
        if (type instanceof ParameterizedType parameterizedType)
        {
            return (Class<?>) parameterizedType.getRawType();
        }
        throw new IllegalArgumentException("当前类型:" + type);
    }

    private static Type resolverSource(Type type)
    {
        Class<?> rawType;
        if (type instanceof ParameterizedType parameterizedType)
        {
            rawType = (Class<?>) parameterizedType.getRawType();
        }
        else if (type instanceof Class<?> clazz)
        {
            rawType = clazz;
        }
        else
        {
            return type;
        }
        if (Collection.class.isAssignableFrom(rawType) || Map.class.isAssignableFrom(rawType))
        {
            return Object.class;
        }
        return type;
    }
}
