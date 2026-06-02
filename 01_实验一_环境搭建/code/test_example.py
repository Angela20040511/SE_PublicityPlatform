#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
简单的Python测试代码示例
"""

def add(a, b):
    """计算两个数的和"""
    return a + b


def subtract(a, b):
    """计算两个数的差"""
    return a - b


def multiply(a, b):
    """计算两个数的积"""
    return a * b


def divide(a, b):
    """计算两个数的商"""
    if b == 0:
        raise ValueError("除数不能为0")
    return a / b


def test_add():
    """测试add函数"""
    assert add(2, 3) == 5
    assert add(-1, 1) == 0
    assert add(0, 0) == 0
    print("✓ add函数测试通过")


def test_subtract():
    """测试subtract函数"""
    assert subtract(5, 3) == 2
    assert subtract(0, 5) == -5
    assert subtract(10, 10) == 0
    print("✓ subtract函数测试通过")


def test_multiply():
    """测试multiply函数"""
    assert multiply(3, 4) == 12
    assert multiply(-2, 3) == -6
    assert multiply(0, 100) == 0
    print("✓ multiply函数测试通过")


def test_divide():
    """测试divide函数"""
    assert divide(10, 2) == 5
    assert divide(7, 2) == 3.5
    assert divide(-10, 2) == -5
    print("✓ divide函数测试通过")


def test_divide_by_zero():
    """测试divide函数的异常情况"""
    try:
        divide(10, 0)
        assert False, "应该抛出ValueError异常"
    except ValueError:
        print("✓ divide函数异常处理测试通过")


def main():
    """主测试函数"""
    print("开始运行测试...\n")
    
    test_add()
    test_subtract()
    test_multiply()
    test_divide()
    test_divide_by_zero()
    
    print("\n所有测试都通过了！")


if __name__ == "__main__":
    main()
